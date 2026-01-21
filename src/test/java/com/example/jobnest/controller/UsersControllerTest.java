package com.example.jobnest.controller;

import com.example.jobnest.entity.Users;
import com.example.jobnest.entity.UsersType;
import com.example.jobnest.services.AuthenticationService;
import com.example.jobnest.services.UsersService;
import com.example.jobnest.services.UsersTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(UsersController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersTypeService usersTypeService;

    @MockBean
    private UsersService usersService;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    void register_rendersViewWithDefaults() throws Exception {
        when(usersTypeService.getAll()).thenReturn(List.of(new UsersType()));

        mockMvc.perform(get("/register").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("getAllTypes"))
                .andExpect(model().attributeExists("userRegistrationRequest"))
                .andExpect(model().attribute("role", "CANDIDATE"));
    }

    @Test
    void userRegistration_returnsViewOnValidationError() throws Exception {
        when(usersTypeService.getAll()).thenReturn(List.of(new UsersType()));

        mockMvc.perform(post("/register/new").with(csrf())
                        .param("email", "bad-email")
                        .param("role", "CANDIDATE"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("getAllTypes"))
                .andExpect(model().attribute("role", "CANDIDATE"));
    }

    @Test
    void userRegistration_redirectsOnSuccess() throws Exception {
        Users savedUser = new Users();
        when(usersService.registerUser(any())).thenReturn(savedUser);
        doNothing().when(authenticationService).authenticateAndCreateSession(any(), any());

        mockMvc.perform(post("/register/new").with(csrf())
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("company", "ACME")
                        .param("email", "john@example.com")
                        .param("password", "secret1")
                        .param("role", "CANDIDATE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void userRegistration_returnsViewOnServiceError() throws Exception {
        when(usersTypeService.getAll()).thenReturn(List.of(new UsersType()));
        when(usersService.registerUser(any())).thenThrow(new RuntimeException("Failed"));

        mockMvc.perform(post("/register/new").with(csrf())
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("company", "ACME")
                        .param("email", "john@example.com")
                        .param("password", "secret1")
                        .param("role", "CANDIDATE"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("getAllTypes"))
                .andExpect(model().attribute("role", "CANDIDATE"))
                .andExpect(model().attributeExists("error"));
    }
}
