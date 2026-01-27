package com.example.jobnest.controller;

import com.example.jobnest.dto.request.ProfileUpdateRequest;
import com.example.jobnest.services.ProfilePageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller for profile management.
 * Follows clean architecture - delegates all business logic to services.
 */
@Controller
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ProfileController {

    private final ProfilePageService profilePageService;

    @GetMapping("/profile/edit")
    public String editProfile(Model model) {
        ProfilePageService.PageResult result = profilePageService.editProfilePage();
        model.addAllAttributes(result.model());
        return result.viewName();
    }

    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute ProfileUpdateRequest request, Model model) {
        ProfilePageService.PageResult result = profilePageService.updateProfile(request);
        model.addAllAttributes(result.model());
        return result.viewName();
    }
}
