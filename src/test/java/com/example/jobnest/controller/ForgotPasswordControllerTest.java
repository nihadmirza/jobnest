package com.example.jobnest.controller;

import com.example.jobnest.services.PasswordResetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.jobnest.dto.request.PasswordResetConfirmRequest;
import com.example.jobnest.exception.InvalidTokenException;
import com.example.jobnest.exception.ValidationException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(ForgotPasswordController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings("null")
class ForgotPasswordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordResetService passwordResetService;

    @Test
    @WithMockUser
    void showForgotPasswordForm_rendersView() throws Exception {
        mockMvc.perform(get("/forgot-password").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("forgot-password"))
                .andExpect(model().attributeExists("request"));
    }

    @Test
    @WithMockUser
    void processForgotPassword_returnsViewOnValidationError() throws Exception {
        mockMvc.perform(post("/forgot-password").with(csrf())
                        .param("email", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("forgot-password"))
                .andExpect(model().attributeHasFieldErrors("request", "email"));

        verifyNoInteractions(passwordResetService);
    }

    @Test
    @WithMockUser
    void processForgotPassword_redirectsOnSuccess() throws Exception {
        doNothing().when(passwordResetService).initiatePasswordReset("user@example.com");

        mockMvc.perform(post("/forgot-password").with(csrf())
                        .param("email", "user@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forgot-password"))
                .andExpect(flash().attributeExists("success"));

        verify(passwordResetService).initiatePasswordReset("user@example.com");
    }

    @Test
    @WithMockUser
    void processForgotPassword_redirectsOnServiceError() throws Exception {
        doThrow(new ValidationException("Boom"))
                .when(passwordResetService).initiatePasswordReset("user@example.com");

        mockMvc.perform(post("/forgot-password").with(csrf())
                        .param("email", "user@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));
    }

    @Test
    @WithMockUser
    void showResetPasswordForm_redirectsWhenTokenInvalid() throws Exception {
        doThrow(new InvalidTokenException("Invalid or expired password reset link. Please request a new one."))
                .when(passwordResetService).getResetRequest("bad-token");

        mockMvc.perform(get("/reset-password").param("token", "bad-token").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forgot-password"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithMockUser
    void showResetPasswordForm_rendersViewWhenTokenValid() throws Exception {
        PasswordResetConfirmRequest req = new PasswordResetConfirmRequest();
        req.setToken("valid-token");
        org.mockito.Mockito.when(passwordResetService.getResetRequest("valid-token")).thenReturn(req);

        mockMvc.perform(get("/reset-password").param("token", "valid-token").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("reset-password"))
                .andExpect(model().attributeExists("request"))
                .andExpect(model().attribute("token", "valid-token"));
    }

    @Test
    @WithMockUser
    void processResetPassword_returnsViewOnValidationError() throws Exception {
        mockMvc.perform(post("/reset-password").with(csrf())
                        .param("token", "t1")
                        .param("password", "secret1")
                        .param("confirmPassword", "mismatch"))
                .andExpect(status().isOk())
                .andExpect(view().name("reset-password"))
                .andExpect(model().attributeHasFieldErrors("request", "passwordsMatch"))
                .andExpect(model().attribute("token", "t1"));

        verifyNoInteractions(passwordResetService);
    }

    @Test
    @WithMockUser
    void processResetPassword_redirectsOnSuccess() throws Exception {
        doNothing().when(passwordResetService).resetPassword("t2", "secret1");

        mockMvc.perform(post("/reset-password").with(csrf())
                        .param("token", "t2")
                        .param("password", "secret1")
                        .param("confirmPassword", "secret1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("success"));

        verify(passwordResetService).resetPassword("t2", "secret1");
    }

    @Test
    @WithMockUser
    void processResetPassword_returnsViewOnServiceError() throws Exception {
        doThrow(new ValidationException("Reset failed"))
                .when(passwordResetService).resetPassword(anyString(), anyString());

        mockMvc.perform(post("/reset-password").with(csrf())
                        .param("token", "t3")
                        .param("password", "secret1")
                        .param("confirmPassword", "secret1"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));
    }
}
