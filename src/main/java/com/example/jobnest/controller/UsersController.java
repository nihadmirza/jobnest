package com.example.jobnest.controller;

import com.example.jobnest.dto.request.UserRegistrationRequest;
import com.example.jobnest.entity.Users;
import com.example.jobnest.entity.UsersType;
import com.example.jobnest.services.AuthenticationService;
import com.example.jobnest.services.UsersService;
import com.example.jobnest.services.UsersTypeService;
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

import java.util.List;

/**
 * Controller for user registration.
 * Follows clean architecture - only handles HTTP concerns.
 */
@Controller
@RequiredArgsConstructor
public class UsersController {

    private static final String GET_ALL_TYPES_ATTR = "getAllTypes";
    private static final String REGISTER_VIEW = "register";
    // private static final String USER_ATTR = "user"; // Removed unused constant
    private static final String ROLE_ATTR = "role";
    private static final String ERROR_ATTR = "error";

    private final UsersTypeService usersTypeService;
    private final UsersService usersService;
    private final AuthenticationService authenticationService;

    @GetMapping("/register")
    public String register(@RequestParam(required = false, defaultValue = "CANDIDATE") String role, Model model) {
        List<UsersType> usersTypes = usersTypeService.getAll();
        model.addAttribute(GET_ALL_TYPES_ATTR, usersTypes);
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setRole(role);
        model.addAttribute("userRegistrationRequest", request);
        model.addAttribute(ROLE_ATTR, role);
        return REGISTER_VIEW;
    }

    @PostMapping("/register/new")
    public String userRegistration(
            @Valid @ModelAttribute("userRegistrationRequest") UserRegistrationRequest request,
            BindingResult bindingResult,
            Model model,
            HttpServletRequest httpRequest) {

        if (bindingResult.hasErrors()) {
            // Return to view with errors; Spring automatically keeps
            // 'userRegistrationRequest' and 'bindingResult' in model
            model.addAttribute(GET_ALL_TYPES_ATTR, usersTypeService.getAll());
            model.addAttribute(ROLE_ATTR, request.getRole());
            return REGISTER_VIEW;
        }

        try {
            Users savedUser = usersService.registerUser(request);
            authenticationService.authenticateAndCreateSession(savedUser, httpRequest);
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute(GET_ALL_TYPES_ATTR, usersTypeService.getAll());
            model.addAttribute(ROLE_ATTR, request.getRole());
            model.addAttribute(ERROR_ATTR, e.getMessage());
            return REGISTER_VIEW;
        }
    }
}
