package com.example.jobnest.services.impl;

import com.example.jobnest.dto.response.CandidateProfilePageDTO;
import com.example.jobnest.dto.response.RecruiterApplicationStatsDTO;
import com.example.jobnest.dto.response.RecruiterApplicationsPageDTO;
import com.example.jobnest.entity.JobSeekerApply;
import com.example.jobnest.entity.JobSeekerProfile;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.entity.Users;
import com.example.jobnest.exception.UnauthorizedException;
import com.example.jobnest.services.ApplicationManagementService;
import com.example.jobnest.services.AuthenticationService;
import com.example.jobnest.services.ProfileService;
import com.example.jobnest.services.RecruiterApplicationsPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecruiterApplicationsPageServiceImpl implements RecruiterApplicationsPageService {

    private static final String APPLICATIONS_VIEW = "recruiter-applications";
    private static final String CANDIDATE_PROFILE_VIEW = "candidate-profile-view";

    private final ApplicationManagementService applicationManagementService;
    private final AuthenticationService authenticationService;
    private final ProfileService profileService;

    @Override
    @Transactional(readOnly = true)
    public PageResult viewApplications(Integer jobId, String status) {
        int recruiterId = getRecruiterAccountId();
        RecruiterApplicationStatsDTO stats = applicationManagementService.getApplicationsWithStats(recruiterId, jobId,
                status);

        RecruiterApplicationsPageDTO page = RecruiterApplicationsPageDTO.builder()
                .stats(stats)
                .selectedJobId(jobId)
                .selectedStatus(status != null ? status : "All")
                .build();

        return new PageResult(APPLICATIONS_VIEW, Map.of("page", page));
    }

    @Override
    @Transactional
    public ActionResult updateApplicationStatus(int applyId, String status, Integer jobId, String filterStatus) {
        int recruiterId = getRecruiterAccountId();
        applicationManagementService.updateApplicationStatus(applyId, status, recruiterId);

        String redirectTo = buildApplicationsRedirect(jobId, filterStatus);
        return new ActionResult(redirectTo, "success", "Application status updated successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult viewCandidateProfile(int applyId) {
        int recruiterId = getRecruiterAccountId();
        JobSeekerApply application = applicationManagementService.getApplicationForRecruiter(applyId, recruiterId);
        JobSeekerProfile candidateProfile = profileService.getJobSeekerProfile(application.getUser().getUserId());

        CandidateProfilePageDTO page = CandidateProfilePageDTO.builder()
                .application(application)
                .candidateProfile(candidateProfile)
                .candidateUser(application.getUser())
                .job(application.getJob())
                .build();

        return new PageResult(CANDIDATE_PROFILE_VIEW, Map.of("page", page));
    }

    private int getRecruiterAccountId() {
        Users user = authenticationService.getCurrentAuthenticatedUser();
        if (user == null) {
            throw new UnauthorizedException("Authentication required.");
        }
        RecruiterProfile recruiterProfile = profileService.getRecruiterProfile(user.getUserId());
        return recruiterProfile.getUserAccountId();
    }

    private String buildApplicationsRedirect(Integer jobId, String filterStatus) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/recruiter/applications");

        if (jobId != null && jobId > 0) {
            builder.queryParam("jobId", jobId);
        }
        if (filterStatus != null && !filterStatus.isEmpty() && !"All".equals(filterStatus)) {
            builder.queryParam("status", filterStatus);
        }

        return "redirect:" + builder.toUriString();
    }
}
