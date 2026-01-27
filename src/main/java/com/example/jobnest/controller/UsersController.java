package com.example.jobnest.controller;

import com.example.jobnest.dto.response.RegisterPageDTO;
import com.example.jobnest.services.RegistrationPageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for user registration.
 * Follows clean architecture - only handles HTTP concerns.
 */
@Controller
@RequiredArgsConstructor
@SuppressWarnings("null")
public class UsersController {

    private final RegistrationPageService registrationPageService;

    @GetMapping("/register")
    public String register(@RequestParam(required = false, defaultValue = "CANDIDATE") String role, Model model) {
        RegistrationPageService.PageResult result = registrationPageService.showRegisterPage(role);
        model.addAllAttributes(result.model());
        return result.viewName();
    }

    @PostMapping("/register/new")
    public String userRegistration(
            @Valid @ModelAttribute("page") RegisterPageDTO page,
            BindingResult bindingResult,
            Model model,
            HttpServletRequest httpRequest) {

        if (bindingResult.hasErrors()) {
            RegistrationPageService.PageResult result = registrationPageService.showRegisterPage(page);
            model.addAllAttributes(result.model());
            return result.viewName();
        }

        RegistrationPageService.PageResult result = registrationPageService.registerNewUser(page, httpRequest);
        model.addAllAttributes(result.model());
        return result.viewName();
    }
}
