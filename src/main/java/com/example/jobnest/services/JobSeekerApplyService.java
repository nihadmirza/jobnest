package com.example.jobnest.services;

import com.example.jobnest.entity.JobSeekerApply;

import com.example.jobnest.exception.BusinessException;
import com.example.jobnest.exception.ResourceNotFoundException;
import com.example.jobnest.exception.ValidationException;

import java.util.List;
import java.util.Optional;

public interface JobSeekerApplyService {

    JobSeekerApply applyToJob(int jobId, int userId, String coverLetter)
            throws ResourceNotFoundException, BusinessException;

    boolean hasUserApplied(int jobId, int userId);

    Optional<JobSeekerApply> findByJobIdAndUserId(int jobId, int userId);

    List<JobSeekerApply> getApplicationsByRecruiterId(int recruiterId);

    List<JobSeekerApply> getApplicationsByRecruiterIdAndJobId(int recruiterId, int jobId);

    List<JobSeekerApply> getApplicationsByRecruiterIdAndStatus(int recruiterId, String status);

    long getApplicationCountByRecruiterId(int recruiterId);

    long getApplicationCountByRecruiterIdAndStatus(int recruiterId, String status);

    JobSeekerApply updateApplicationStatus(int applyId, String status)
            throws ResourceNotFoundException, ValidationException;

    Optional<JobSeekerApply> findById(int applyId);

    List<JobSeekerApply> getApplicationsByUserId(int userId);

    long getTotalApplicationsCount();

    List<JobSeekerApply> getAllApplications();
}
