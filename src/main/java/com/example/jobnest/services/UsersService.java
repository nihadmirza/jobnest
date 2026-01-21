package com.example.jobnest.services;

import com.example.jobnest.dto.request.UserRegistrationRequest;
import com.example.jobnest.entity.JobSeekerProfile;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.entity.Users;

import java.util.List;
import java.util.Optional;

public interface UsersService {

    Users getCurrentUser();

    long getTotalUsersCount();

    /**
     * Register a new user using DTO.
     *
     * @param request Registration request DTO
     * @return Created user
     */
    Users registerUser(UserRegistrationRequest request);

    /**
     * Creates a new user with the given details.
     * 
     * @deprecated Use {@link #registerUser(UserRegistrationRequest)} instead.
     *             This method will be removed in version 2.0.0
     * @param users     The user entity
     * @param firstName User's first name
     * @param lastName  User's last name
     * @param company   Company name (if recruiter)
     * @return Created user entity
     */
    @Deprecated(since = "1.5.0", forRemoval = true)
    // TODO: Remove this method in version 2.0. Use registerUser() instead.
    Users addNew(Users users, String firstName, String lastName, String company);

    Optional<Users> getUserByEmail(String email);

    Optional<JobSeekerProfile> getJobSeekerProfileByUserId(int userId);

    Optional<RecruiterProfile> getRecruiterProfileByUserId(int userId);

    JobSeekerProfile updateJobSeekerProfile(JobSeekerProfile profile);

    RecruiterProfile updateRecruiterProfile(RecruiterProfile profile);

    List<Users> getAllUsers();
}
