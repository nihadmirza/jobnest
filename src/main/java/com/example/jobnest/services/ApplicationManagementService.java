package com.example.jobnest.services;

import com.example.jobnest.dto.response.RecruiterApplicationStatsDTO;
import com.example.jobnest.entity.JobSeekerApply;

import com.example.jobnest.exception.ResourceNotFoundException;
import com.example.jobnest.exception.UnauthorizedException;
import com.example.jobnest.exception.ValidationException;

/**
 * Service for managing job applications from recruiter perspective.
 */
public interface ApplicationManagementService {

    /**
     * Get applications with statistics for recruiter.
     *
     * @param recruiterId Recruiter ID
     * @param jobId       Optional job ID filter
     * @param status      Optional status filter
     * @return Application data with statistics
     */
    RecruiterApplicationStatsDTO getApplicationsWithStats(int recruiterId, Integer jobId, String status);

    /**
     * Update application status with authorization check.
     *
     * @param applyId     Application ID
     * @param status      New status
     * @param recruiterId Recruiter ID (for authorization)
     */
    void updateApplicationStatus(int applyId, String status, int recruiterId)
            throws ResourceNotFoundException, ValidationException, UnauthorizedException;

    /**
     * Get application for recruiter with authorization check.
     *
     * @param applyId     Application ID
     * @param recruiterId Recruiter ID (for authorization)
     * @return Application
     */
    JobSeekerApply getApplicationForRecruiter(int applyId, int recruiterId);
}
