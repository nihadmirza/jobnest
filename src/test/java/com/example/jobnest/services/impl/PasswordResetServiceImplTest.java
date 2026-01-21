package com.example.jobnest.services.impl;

import com.example.jobnest.entity.PasswordResetToken;
import com.example.jobnest.entity.Users;
import com.example.jobnest.exception.ValidationException;
import com.example.jobnest.repository.PasswordResetTokenRepository;
import com.example.jobnest.repository.UsersRepository;
import com.example.jobnest.services.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceImplTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetServiceImpl passwordResetService;

    @Captor
    private ArgumentCaptor<String> emailCaptor;

    @Captor
    private ArgumentCaptor<String> linkCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(passwordResetService, "appUrl", "http://localhost:8080");
    }

    @Test
    void initiatePasswordReset_returnsSilentlyWhenEmailNotFound() {
        when(usersRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        passwordResetService.initiatePasswordReset("missing@example.com");

        verify(tokenRepository, never()).save(any());
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    void initiatePasswordReset_createsTokenAndSendsEmail() {
        Users user = new Users();
        user.setUserId(10);
        user.setEmail("user@example.com");

        when(usersRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        passwordResetService.initiatePasswordReset("user@example.com");

        verify(tokenRepository).deleteByUser_UserId(10);
        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendPasswordResetEmail(emailCaptor.capture(), linkCaptor.capture());
        assertTrue(linkCaptor.getValue().startsWith("http://localhost:8080/reset-password?token="));
    }

    @Test
    void initiatePasswordReset_throwsWhenEmailSendFails() {
        Users user = new Users();
        user.setUserId(10);
        user.setEmail("user@example.com");

        when(usersRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        org.mockito.Mockito.doThrow(new RuntimeException("mail down"))
                .when(emailService).sendPasswordResetEmail(anyString(), anyString());

        assertThrows(ValidationException.class,
                () -> passwordResetService.initiatePasswordReset("user@example.com"));
    }

    @Test
    void validateResetToken_returnsFalseForNullOrEmpty() {
        assertFalse(passwordResetService.validateResetToken(null));
        assertFalse(passwordResetService.validateResetToken("   "));
    }

    @Test
    void validateResetToken_handlesMissingAndExpired() {
        when(tokenRepository.findByToken("missing")).thenReturn(Optional.empty());
        assertFalse(passwordResetService.validateResetToken("missing"));

        Users user = new Users();
        PasswordResetToken expired = new PasswordResetToken(user);
        expired.setExpiryDate(new Date(System.currentTimeMillis() - 1000));

        when(tokenRepository.findByToken("expired")).thenReturn(Optional.of(expired));
        assertFalse(passwordResetService.validateResetToken("expired"));
        verify(tokenRepository).delete(expired);
    }

    @Test
    void validateResetToken_returnsTrueForValid() {
        Users user = new Users();
        PasswordResetToken valid = new PasswordResetToken(user);
        valid.setExpiryDate(new Date(System.currentTimeMillis() + 100000));

        when(tokenRepository.findByToken("valid")).thenReturn(Optional.of(valid));
        assertTrue(passwordResetService.validateResetToken("valid"));
    }

    @Test
    void resetPassword_throwsForInvalidOrExpiredToken() {
        when(tokenRepository.findByToken("missing")).thenReturn(Optional.empty());
        assertThrows(ValidationException.class,
                () -> passwordResetService.resetPassword("missing", "newPass"));

        Users user = new Users();
        PasswordResetToken expired = new PasswordResetToken(user);
        expired.setExpiryDate(new Date(System.currentTimeMillis() - 1000));
        when(tokenRepository.findByToken("expired")).thenReturn(Optional.of(expired));

        assertThrows(ValidationException.class,
                () -> passwordResetService.resetPassword("expired", "newPass"));
        verify(tokenRepository).delete(expired);
    }

    @Test
    void resetPassword_updatesPasswordAndDeletesToken() {
        Users user = new Users();
        PasswordResetToken token = new PasswordResetToken(user);
        token.setExpiryDate(new Date(System.currentTimeMillis() + 100000));

        when(tokenRepository.findByToken("token")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newPass")).thenReturn("encoded");

        passwordResetService.resetPassword("token", "newPass");

        verify(usersRepository).save(user);
        verify(tokenRepository).delete(token);
    }
}
