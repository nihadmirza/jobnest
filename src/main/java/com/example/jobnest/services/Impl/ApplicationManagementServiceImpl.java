package com.example.jobnest.services.impl;

import com.example.jobnest.dto.response.RecruiterApplicationStatsDTO;
import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.JobSeekerApply;
import com.example.jobnest.entity.JobSeekerProfile;
import com.example.jobnest.exception.ResourceNotFoundException;
import com.example.jobnest.exception.UnauthorizedException;
import com.example.jobnest.exception.ValidationException;
import com.example.jobnest.repository.JobSeekerProfileRepository;
import com.example.jobnest.services.ApplicationManagementService;
import com.example.jobnest.services.JobSeekerApplyService;
import com.example.jobnest.services.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of ApplicationManagementService.
 */
@Service
@RequiredArgsConstructor
public class ApplicationManagementServiceImpl implements ApplicationManagementService {

    private final JobSeekerApplyService jobSeekerApplyService;
    private final JobService jobService;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;

    private ApplicationManagementService self;

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.context.annotation.Lazy
    public void setSelf(ApplicationManagementService self) {
        this.self = self;
    }

    @Override
    @Transactional(readOnly = true)
    public RecruiterApplicationStatsDTO getApplicationsWithStats(int recruiterId, Integer jobId, String status) {
        // Apply filters to get applications
        List<JobSeekerApply> applications;

        if (jobId != null && jobId > 0) {
            applications = jobSeekerApplyService.getApplicationsByRecruiterIdAndJobId(recruiterId, jobId);
        } else if (status != null && !status.isEmpty() && !status.equals("All")) {
            applications = jobSeekerApplyService.getApplicationsByRecruiterIdAndStatus(recruiterId, status);
        } else {
            applications = jobSeekerApplyService.getApplicationsByRecruiterId(recruiterId);
        }

        // Get all jobs for filter dropdown
        List<Job> allJobs = jobService.findJobsByRecruiterId(recruiterId);

        // Get candidate profiles for each application
        Map<Integer, JobSeekerProfile> candidateProfilesMap = new HashMap<>();
        for (JobSeekerApply application : applications) {
            Optional<JobSeekerProfile> candidateProfile = jobSeekerProfileRepository
                    .findById(application.getUser().getUserId());
            candidateProfile.ifPresent(profile -> candidateProfilesMap.put(application.getUser().getUserId(), profile));
        }

        // Get counts for badges
        long totalApplications = jobSeekerApplyService.getApplicationCountByRecruiterId(recruiterId);
        long pendingCount = jobSeekerApplyService.getApplicationCountByRecruiterIdAndStatus(recruiterId, "Pending");
        long reviewedCount = jobSeekerApplyService.getApplicationCountByRecruiterIdAndStatus(recruiterId, "Reviewed");
        long acceptedCount = jobSeekerApplyService.getApplicationCountByRecruiterIdAndStatus(recruiterId, "Accepted");
        long rejectedCount = jobSeekerApplyService.getApplicationCountByRecruiterIdAndStatus(recruiterId, "Rejected");

        return RecruiterApplicationStatsDTO.builder()
                .applications(applications)
                .candidateProfilesMap(candidateProfilesMap)
                .allJobs(allJobs)
                .totalApplications(totalApplications)
                .pendingCount(pendingCount)
                .reviewedCount(reviewedCount)
                .acceptedCount(acceptedCount)
                .rejectedCount(rejectedCount)
                .build();
    }

    @Override
    @Transactional
    public void updateApplicationStatus(int applyId, String status, int recruiterId)
            throws ResourceNotFoundException, ValidationException, UnauthorizedException {
        self.getApplicationForRecruiter(applyId, recruiterId);
        jobSeekerApplyService.updateApplicationStatus(applyId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public JobSeekerApply getApplicationForRecruiter(int applyId, int recruiterId) {
        Optional<JobSeekerApply> applicationOptional = jobSeekerApplyService.findById(applyId);

        if (applicationOptional.isEmpty()) {
            throw new ResourceNotFoundException("Application", applyId);
        }

        JobSeekerApply application = applicationOptional.get();

        // Verify the application belongs to this recruiter
        if (application.getJob().getRecruiter().getUserAccountId() != recruiterId) {
            throw new UnauthorizedException("You don't have permission to access this application");
        }

        return application;
    }
}
