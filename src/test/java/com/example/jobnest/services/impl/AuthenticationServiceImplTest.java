package com.example.jobnest.services.impl;

import com.example.jobnest.entity.Users;
import com.example.jobnest.services.UsersService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private UsersService usersService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void isAuthenticated_returnsFalseForAnonymous() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("anonymousUser", null));
        assertFalse(authenticationService.isAuthenticated());
    }

    @Test
    void isAuthenticated_returnsTrueForValidUser() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "user@example.com", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertTrue(authenticationService.isAuthenticated());
    }

    @Test
    void getCurrentAuthenticatedUser_returnsNullWhenNotAuthenticated() {
        SecurityContextHolder.clearContext();
        assertNull(authenticationService.getCurrentAuthenticatedUser());
    }

    @Test
    void hasRole_checksAuthorities() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "user@example.com", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertTrue(authenticationService.hasRole("ADMIN"));
        assertFalse(authenticationService.hasRole("RECRUITER"));
    }

    @Test
    void authenticateAndCreateSession_setsSecurityContext() {
        Users user = new Users();
        user.setEmail("user@example.com");

        UserDetails details = User.withUsername("user@example.com")
                .password("pass")
                .authorities("ROLE_USER")
                .build();
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(details);

        HttpServletRequest request = mock(HttpServletRequest.class);
        authenticationService.authenticateAndCreateSession(user, request);

        assertEquals("user@example.com", SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
