package com.example.jobnest.services;

/**
 * Service for password reset operations.
 */
public interface PasswordResetService {

    /**
     * Initiate password reset process by creating token and sending email.
     *
     * @param email User's email address
     */
    void initiatePasswordReset(String email);

    /**
     * Validate if reset token is valid and not expired.
     *
     * @param token Reset token
     * @return true if token is valid
     */
    boolean validateResetToken(String token);

    /**
     * Reset user password using valid token.
     *
     * @param token       Reset token
     * @param newPassword New password
     */
    void resetPassword(String token, String newPassword);
}
