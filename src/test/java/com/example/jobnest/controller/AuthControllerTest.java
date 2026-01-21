package com.example.jobnest.controller;

import com.example.jobnest.config.CustomAuthenticationFailureHandler;
import com.example.jobnest.config.CustomAuthenticationSuccessHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.jobnest.services.AuthenticationService;
import com.example.jobnest.services.UsersService;
import org.springframework.security.core.userdetails.UserDetailsService;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private UsersService usersService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private CustomAuthenticationFailureHandler failureHandler;

    @MockBean
    private CustomAuthenticationSuccessHandler successHandler;

    @Test
    public void testLogin() throws Exception {
        mockMvc.perform(get("/login").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("role"))
                .andExpect(model().attribute("role", "CANDIDATE"));
    }

    @Test
    public void testLoginWithError() throws Exception {
        mockMvc.perform(get("/login").param("error", "true").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Invalid email or password."));
    }

    @Test
    public void testLoginWithBlocked() throws Exception {
        mockMvc.perform(get("/login").param("blocked", "true").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Too many failed attempts. Account blocked for 15 minutes."));
    }
}
