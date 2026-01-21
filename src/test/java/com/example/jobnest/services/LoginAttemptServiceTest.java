
package com.example.jobnest.services;

import com.example.jobnest.services.impl.LoginAttemptServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginAttemptServiceTest {

    private LoginAttemptServiceImpl loginAttemptService;

    @BeforeEach
    void setUp() {
        loginAttemptService = new LoginAttemptServiceImpl();
    }

    @Test
    void testLoginSucceededResetsAttempts() {
        String key = "127.0.0.1";
        loginAttemptService.loginFailed(key);
        loginAttemptService.loginFailed(key);

        loginAttemptService.loginSucceeded(key);

        loginAttemptService.loginFailed(key);
        loginAttemptService.loginFailed(key);
        loginAttemptService.loginFailed(key);

        assertFalse(loginAttemptService.isBlocked(key));
    }

    @Test
    void testBlockingAfterMaxAttempts() {
        String key = "192.168.1.1";
        for (int i = 0; i < 5; i++) {
            assertFalse(loginAttemptService.isBlocked(key));
            loginAttemptService.loginFailed(key);
        }

        assertTrue(loginAttemptService.isBlocked(key));
    }
}
