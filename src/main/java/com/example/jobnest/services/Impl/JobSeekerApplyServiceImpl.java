package com.example.jobnest.services.impl;

import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.JobSeekerApply;
import com.example.jobnest.entity.Users;
import com.example.jobnest.exception.BusinessException;
import com.example.jobnest.exception.ResourceNotFoundException;
import com.example.jobnest.exception.ValidationException;
import com.example.jobnest.repository.JobRepository;
import com.example.jobnest.repository.JobSeekerApplyRepository;
import com.example.jobnest.repository.UsersRepository;
import com.example.jobnest.services.JobSeekerApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobSeekerApplyServiceImpl implements JobSeekerApplyService {

    private final JobSeekerApplyRepository jobSeekerApplyRepository;
    private final JobRepository jobRepository;
    private final UsersRepository usersRepository;

    @Override
    @Transactional
    public JobSeekerApply applyToJob(int jobId, int userId, String coverLetter)
            throws ResourceNotFoundException, BusinessException {
        // Check if user already applied
        if (hasUserApplied(jobId, userId)) {
            throw new BusinessException("You have already applied to this job");
        }

        // Get job and user
        Optional<Job> jobOptional = jobRepository.findById(jobId);
        if (jobOptional.isEmpty()) {
            throw new ResourceNotFoundException("Job", jobId);
        }

        Optional<Users> userOptional = usersRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User", userId);
        }

        Job job = jobOptional.get();
        Users user = userOptional.get();

        // Check if job is active
        if (!job.isActive()) {
            throw new BusinessException("This job is no longer available");
        }

        // Create application
        JobSeekerApply application = new JobSeekerApply();
        application.setJob(job);
        application.setUser(user);
        application.setCoverLetter(coverLetter);
        application.setStatus("Pending");

        return jobSeekerApplyRepository.save(application);
    }

    @Override
    public boolean hasUserApplied(int jobId, int userId) {
        return jobSeekerApplyRepository.existsByJob_JobIdAndUser_UserId(jobId, userId);
    }

    @Override
    public Optional<JobSeekerApply> findByJobIdAndUserId(int jobId, int userId) {
        return jobSeekerApplyRepository.findByJobIdAndUserId(jobId, userId);
    }

    @Override
    public List<JobSeekerApply> getApplicationsByRecruiterId(int recruiterId) {
        return jobSeekerApplyRepository.findByRecruiterId(recruiterId);
    }

    @Override
    public List<JobSeekerApply> getApplicationsByRecruiterIdAndJobId(int recruiterId, int jobId) {
        return jobSeekerApplyRepository.findByRecruiterIdAndJobId(recruiterId, jobId);
    }

    @Override
    public List<JobSeekerApply> getApplicationsByRecruiterIdAndStatus(int recruiterId, String status) {
        return jobSeekerApplyRepository.findByRecruiterIdAndStatus(recruiterId, status);
    }

    @Override
    public long getApplicationCountByRecruiterId(int recruiterId) {
        return jobSeekerApplyRepository.countByRecruiterId(recruiterId);
    }

    @Override
    public long getApplicationCountByRecruiterIdAndStatus(int recruiterId, String status) {
        return jobSeekerApplyRepository.countByRecruiterIdAndStatus(recruiterId, status);
    }

    @Override
    @Transactional
    public JobSeekerApply updateApplicationStatus(int applyId, String status)
            throws ResourceNotFoundException, ValidationException {
        Optional<JobSeekerApply> applicationOptional = jobSeekerApplyRepository.findById(applyId);
        if (applicationOptional.isEmpty()) {
            throw new ResourceNotFoundException("Application", applyId);
        }

        JobSeekerApply application = applicationOptional.get();

        // Validate status transition
        // (Restriction removed to allow correcting mistakes)

        // Validate new status
        if (!isValidStatus(status)) {
            throw new ValidationException("Invalid status: " + status);
        }

        application.setStatus(status);
        return jobSeekerApplyRepository.save(application);
    }

    @Override
    public Optional<JobSeekerApply> findById(int applyId) {
        return jobSeekerApplyRepository.findById(applyId);
    }

    private boolean isValidStatus(String status) {
        return status != null && (status.equals("Pending") ||
                status.equals("Reviewed") ||
                status.equals("Accepted") ||
                status.equals("Rejected"));
    }

    @Override
    public List<JobSeekerApply> getApplicationsByUserId(int userId) {
        return jobSeekerApplyRepository.findByUserId(userId);
    }

    @Override
    public long getTotalApplicationsCount() {
        return jobSeekerApplyRepository.count();
    }

    @Override
    public List<JobSeekerApply> getAllApplications() {
        return jobSeekerApplyRepository.findAll();
    }
}
