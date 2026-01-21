package com.example.jobnest.services;

import com.example.jobnest.dto.response.JobSeekerDashboardDTO;
import com.example.jobnest.dto.response.RecruiterDashboardDTO;

/**
 * Service for dashboard data aggregation.
 */
public interface DashboardService {

    /**
     * Get dashboard data for recruiter.
     *
     * @param userId User ID
     * @return Recruiter dashboard data
     */
    RecruiterDashboardDTO getRecruiterDashboardData(int userId);

    /**
     * Get dashboard data for job seeker.
     *
     * @param userId User ID
     * @return Job seeker dashboard data
     */
    JobSeekerDashboardDTO getJobSeekerDashboardData(int userId);
}
