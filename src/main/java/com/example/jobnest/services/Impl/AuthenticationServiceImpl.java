package com.example.jobnest.services.impl;

import com.example.jobnest.entity.Users;
import com.example.jobnest.services.AuthenticationService;
import com.example.jobnest.services.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

/**
 * Implementation of AuthenticationService.
 * Handles all authentication and session management logic.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserDetailsService userDetailsService;
    private final UsersService usersService;

    @Override
    public void authenticateAndCreateSession(Users user, HttpServletRequest request) {
        // Load user details from UserDetailsService
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        // Create authentication token
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        auth.setDetails(new WebAuthenticationDetails(request));

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Override
    public Users getCurrentAuthenticatedUser() {
        if (!isAuthenticated()) {
            return null;
        }
        return usersService.getCurrentUser();
    }

    @Override
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !authentication.getName().equals("anonymousUser");
    }

    @Override
    public boolean hasRole(String role) {
        if (!isAuthenticated()) {
            return false;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    @Override
    public String getCurrentUserEmail() {
        if (!isAuthenticated()) {
            return null;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
