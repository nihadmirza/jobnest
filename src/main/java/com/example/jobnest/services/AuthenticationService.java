package com.example.jobnest.services;

import com.example.jobnest.entity.Users;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Service for handling authentication and session management.
 * Separates authentication concerns from controller logic.
 */
public interface AuthenticationService {

    /**
     * Authenticate user and create session.
     *
     * @param user    The authenticated user
     * @param request HTTP request for session creation
     */
    void authenticateAndCreateSession(Users user, HttpServletRequest request);

    /**
     * Get the currently authenticated user.
     *
     * @return Current user or null if not authenticated
     */
    Users getCurrentAuthenticatedUser();

    /**
     * Check if user is authenticated.
     *
     * @return true if user is authenticated
     */
    boolean isAuthenticated();

    /**
     * Check if current user has a specific role.
     *
     * @param role Role name (e.g., "RECRUITER", "JOB SEEKER")
     * @return true if user has the role
     */
    boolean hasRole(String role);

    /**
     * Get current user's email.
     *
     * @return Email of current user or null
     */
    String getCurrentUserEmail();
}
