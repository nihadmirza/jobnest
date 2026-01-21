package com.example.jobnest.controller;

import com.example.jobnest.services.PasswordResetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
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
class ForgotPasswordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordResetService passwordResetService;

    @Test
    void showForgotPasswordForm_rendersView() throws Exception {
        mockMvc.perform(get("/forgot-password").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("forgot-password"))
                .andExpect(model().attributeExists("request"));
    }

    @Test
    void processForgotPassword_returnsViewOnValidationError() throws Exception {
        mockMvc.perform(post("/forgot-password").with(csrf())
                        .param("email", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("forgot-password"))
                .andExpect(model().attributeExists("error"));

        verifyNoInteractions(passwordResetService);
    }

    @Test
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
    void processForgotPassword_redirectsOnServiceError() throws Exception {
        doThrow(new RuntimeException("Boom"))
                .when(passwordResetService).initiatePasswordReset("user@example.com");

        mockMvc.perform(post("/forgot-password").with(csrf())
                        .param("email", "user@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forgot-password"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void showResetPasswordForm_redirectsWhenTokenInvalid() throws Exception {
        when(passwordResetService.validateResetToken("bad-token")).thenReturn(false);

        mockMvc.perform(get("/reset-password").param("token", "bad-token").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forgot-password"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void showResetPasswordForm_rendersViewWhenTokenValid() throws Exception {
        when(passwordResetService.validateResetToken("valid-token")).thenReturn(true);

        mockMvc.perform(get("/reset-password").param("token", "valid-token").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("reset-password"))
                .andExpect(model().attributeExists("request"))
                .andExpect(model().attribute("token", "valid-token"));
    }

    @Test
    void processResetPassword_returnsViewOnValidationError() throws Exception {
        mockMvc.perform(post("/reset-password").with(csrf())
                        .param("token", "t1")
                        .param("password", "secret1")
                        .param("confirmPassword", "mismatch"))
                .andExpect(status().isOk())
                .andExpect(view().name("reset-password"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("token", "t1"));

        verifyNoInteractions(passwordResetService);
    }

    @Test
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
    void processResetPassword_returnsViewOnServiceError() throws Exception {
        doThrow(new RuntimeException("Reset failed"))
                .when(passwordResetService).resetPassword(anyString(), anyString());

        mockMvc.perform(post("/reset-password").with(csrf())
                        .param("token", "t3")
                        .param("password", "secret1")
                        .param("confirmPassword", "secret1"))
                .andExpect(status().isOk())
                .andExpect(view().name("reset-password"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("token", "t3"));
    }
}
