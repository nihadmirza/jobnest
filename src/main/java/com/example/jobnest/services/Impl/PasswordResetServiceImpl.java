package com.example.jobnest.services.impl;

import com.example.jobnest.dto.request.PasswordResetConfirmRequest;
import com.example.jobnest.entity.PasswordResetToken;
import com.example.jobnest.entity.Users;
import com.example.jobnest.exception.InvalidTokenException;
import com.example.jobnest.exception.ValidationException;
import com.example.jobnest.repository.PasswordResetTokenRepository;
import com.example.jobnest.repository.UsersRepository;
import com.example.jobnest.services.EmailService;
import com.example.jobnest.services.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of PasswordResetService.
 */
@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UsersRepository usersRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.url:http://localhost:8080}")
    private String appUrl;

    @Override
    @Transactional
    public void initiatePasswordReset(String email) {
        Optional<Users> userOptional = usersRepository.findByEmail(email.trim().toLowerCase());

        // Don't reveal if email exists (security best practice)
        if (userOptional.isEmpty()) {
            return;
        }

        Users user = userOptional.get();

        // Delete any existing tokens for this user
        tokenRepository.deleteByUser_UserId(user.getUserId());

        // Create new password reset token
        PasswordResetToken resetToken = new PasswordResetToken(user);
        tokenRepository.save(resetToken);

        // Create reset link
        String resetLink = appUrl + "/reset-password?token=" + resetToken.getToken();

        // Send email
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
        } catch (Exception e) {
            throw new ValidationException("An error occurred while sending the email. Please try again.");
        }
    }

    @Override
    public PasswordResetConfirmRequest getResetRequest(String token) {
        if (!validateResetToken(token)) {
            throw new InvalidTokenException("Invalid or expired password reset link. Please request a new one.");
        }
        PasswordResetConfirmRequest request = new PasswordResetConfirmRequest();
        request.setToken(token);
        return request;
    }

    @Override
    public boolean validateResetToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        Optional<PasswordResetToken> tokenOptional = tokenRepository.findByToken(token);

        if (tokenOptional.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = tokenOptional.get();

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOptional = tokenRepository.findByToken(token);

        if (tokenOptional.isEmpty()) {
            throw new ValidationException("Invalid or already used password reset link.");
        }

        PasswordResetToken resetToken = tokenOptional.get();

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new ValidationException("Password reset link has expired. Please request a new one.");
        }

        // Update user password
        Users user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        usersRepository.save(user);

        // Delete the token
        tokenRepository.delete(resetToken);
    }
}
