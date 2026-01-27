package com.example.jobnest.services.impl;

import com.example.jobnest.common.UserType;
import com.example.jobnest.dto.response.JobSeekerDashboardDTO;
import com.example.jobnest.dto.response.RecruiterDashboardDTO;
import com.example.jobnest.dto.response.UnifiedDashboardDTO;
import com.example.jobnest.entity.JobSeekerProfile;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.entity.Users;
import com.example.jobnest.exception.ResourceNotFoundException;
import com.example.jobnest.exception.UnauthorizedException;
import com.example.jobnest.exception.ValidationException;
import com.example.jobnest.repository.JobSeekerProfileRepository;
import com.example.jobnest.repository.RecruiterProfileRepository;
import com.example.jobnest.services.AuthenticationService;
import com.example.jobnest.services.DashboardService;
import com.example.jobnest.services.JobSeekerApplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of DashboardService.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final RecruiterProfileRepository recruiterProfileRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final JobSeekerApplyService jobSeekerApplyService;
    private final AuthenticationService authenticationService;

    @Override
    @Transactional(readOnly = true)
    public UnifiedDashboardDTO getDashboardData() {
        Users user = authenticationService.getCurrentAuthenticatedUser();
        if (user == null) {
            throw new UnauthorizedException("Authentication required to view dashboard.");
        }
        UserType userType = UserType.fromUsersType(user.getUserTypeId())
                .orElseThrow(() -> new ValidationException("User type not found for current user."));

        if (userType == UserType.RECRUITER) {
            RecruiterDashboardDTO recruiter = getRecruiterDashboardData(user.getUserId());
            return UnifiedDashboardDTO.builder()
                    .userType(UserType.RECRUITER)
                    .recruiterProfile(recruiter.getProfile())
                    .applicationCount(recruiter.getApplicationCount())
                    .pendingApplicationCount(recruiter.getPendingApplicationCount())
                    .build();
        }

        JobSeekerDashboardDTO seeker = getJobSeekerDashboardData(user.getUserId());
        return UnifiedDashboardDTO.builder()
                .userType(UserType.JOB_SEEKER)
                .jobSeekerProfile(seeker.getProfile())
                .applications(seeker.getApplications())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RecruiterDashboardDTO getRecruiterDashboardData(int userId) {
        RecruiterProfile profile = recruiterProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter Profile", userId));

        int recruiterId = profile.getUserAccountId();
        long applicationCount = jobSeekerApplyService.getApplicationCountByRecruiterId(recruiterId);
        long pendingCount = jobSeekerApplyService.getApplicationCountByRecruiterIdAndStatus(recruiterId, "Pending");

        return RecruiterDashboardDTO.builder()
                .profile(profile)
                .applicationCount(applicationCount)
                .pendingApplicationCount(pendingCount)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public JobSeekerDashboardDTO getJobSeekerDashboardData(int userId) {
        JobSeekerProfile profile = jobSeekerProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Job Seeker Profile", userId));

        // Force initialization of skills
        if (profile.getSkills() != null && !profile.getSkills().isEmpty()) {
            // Force initialization of skills and log it
            log.debug("User {} has {} skills", userId, profile.getSkills().size());
        }

        return JobSeekerDashboardDTO.builder()
                .profile(profile)
                .applications(jobSeekerApplyService.getApplicationsByUserId(userId))
                .build();
    }
}
