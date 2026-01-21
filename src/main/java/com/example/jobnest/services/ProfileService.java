package com.example.jobnest.services;

import com.example.jobnest.dto.request.ProfileUpdateRequest;
import com.example.jobnest.entity.JobSeekerProfile;
import com.example.jobnest.entity.RecruiterProfile;

/**
 * Service for handling profile management operations.
 */
public interface ProfileService {

    /**
     * Get job seeker profile by user ID.
     *
     * @param userId User ID
     * @return JobSeekerProfile or null
     */
    JobSeekerProfile getJobSeekerProfile(int userId);

    /**
     * Get recruiter profile by user ID.
     *
     * @param userId User ID
     * @return RecruiterProfile or null
     */
    RecruiterProfile getRecruiterProfile(int userId);

    /**
     * Update job seeker profile.
     *
     * @param request Profile update request
     * @param userId  User ID
     * @return Updated profile
     */
    JobSeekerProfile updateJobSeekerProfile(ProfileUpdateRequest request, int userId);

    /**
     * Update recruiter profile.
     *
     * @param request Profile update request
     * @param userId  User ID
     * @return Updated profile
     */
    RecruiterProfile updateRecruiterProfile(ProfileUpdateRequest request, int userId);
}
