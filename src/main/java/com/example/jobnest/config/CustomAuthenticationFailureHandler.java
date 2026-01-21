package com.example.jobnest.config;

import com.example.jobnest.services.LoginAttemptService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final LoginAttemptService loginAttemptService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        String ip = getClientIP(request);
        loginAttemptService.loginFailed(ip);

        // Check if user is now blocked
        if (loginAttemptService.isBlocked(ip)) {
            // We can customize the error message
            // Redirect to login with specific error param
            setDefaultFailureUrl("/login?error=true&blocked=true");
        } else {
            setDefaultFailureUrl("/login?error=true");
        }

        super.onAuthenticationFailure(request, response, exception);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
