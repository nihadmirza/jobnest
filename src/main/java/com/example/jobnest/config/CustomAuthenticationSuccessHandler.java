package com.example.jobnest.config;

import com.example.jobnest.services.LoginAttemptService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final LoginAttemptService loginAttemptService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        String ip = getClientIP(request);
        if (loginAttemptService.isBlocked(ip)) {
            // Even if password is correct, if IP is blocked, deny access
            // Invalidate session/logout is tricky here, so we just redirect to error
            // Ideally we should throw exception before authentication, but this works as a
            // safety net
            response.sendRedirect("/login?error=true&blocked=true");
            return;
        }

        loginAttemptService.loginSucceeded(ip);

        handle(request, response, authentication);
        super.clearAuthenticationAttributes(request);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        String targetUrl = determineTargetUrl(authentication);
        if (response.isCommitted()) {
            return;
        }
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(Authentication authentication) {
        boolean isJobSeeker = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_JOB SEEKER"));
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
        boolean isRecruiter = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_RECRUITER"));

        if (isAdmin) {
            return "/admin/dashboard";
        } else if (isJobSeeker || isRecruiter) {
            return "/dashboard";
        } else {
            return "/";
        }
    }
}
