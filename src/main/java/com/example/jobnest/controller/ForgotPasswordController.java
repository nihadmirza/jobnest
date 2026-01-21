package com.example.jobnest.controller;

import com.example.jobnest.dto.request.PasswordResetConfirmRequest;
import com.example.jobnest.dto.request.PasswordResetRequest;
import com.example.jobnest.services.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for password reset functionality.
 * Follows clean architecture - all business logic delegated to
 * PasswordResetService.
 */
@Controller
@RequiredArgsConstructor
public class ForgotPasswordController {

    private static final String FORGOT_PASSWORD_VIEW = "forgot-password";
    private static final String RESET_PASSWORD_VIEW = "reset-password";
    private static final String ERROR_ATTR = "error";
    private static final String TOKEN_PARAM = "token";

    private final PasswordResetService passwordResetService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("request", new PasswordResetRequest());
        return FORGOT_PASSWORD_VIEW;
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @Valid @ModelAttribute("request") PasswordResetRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(ERROR_ATTR, bindingResult.getAllErrors().get(0).getDefaultMessage());
            return FORGOT_PASSWORD_VIEW;
        }

        try {
            passwordResetService.initiatePasswordReset(request.getEmail());
            redirectAttributes.addFlashAttribute("success",
                    "If this email address is registered, a password reset link will be sent.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_ATTR, e.getMessage());
        }

        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(
            @RequestParam(required = false) String token,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!passwordResetService.validateResetToken(token)) {
            redirectAttributes.addFlashAttribute(ERROR_ATTR,
                    "Invalid or expired password reset link. Please request a new one.");
            return "redirect:/forgot-password";
        }

        PasswordResetConfirmRequest request = new PasswordResetConfirmRequest();
        request.setToken(token);
        model.addAttribute("request", request);
        model.addAttribute(TOKEN_PARAM, token);
        return RESET_PASSWORD_VIEW;
    }

    @PostMapping("/reset-password")
    public String processResetPassword(
            @Valid @ModelAttribute("request") PasswordResetConfirmRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(ERROR_ATTR, bindingResult.getAllErrors().get(0).getDefaultMessage());
            model.addAttribute(TOKEN_PARAM, request.getToken());
            return RESET_PASSWORD_VIEW;
        }

        try {
            passwordResetService.resetPassword(request.getToken(), request.getPassword());
            redirectAttributes.addFlashAttribute("success",
                    "Your password has been successfully updated. You can now login with your new password.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute(ERROR_ATTR, e.getMessage());
            model.addAttribute(TOKEN_PARAM, request.getToken());
            return RESET_PASSWORD_VIEW;
        }
    }
}
