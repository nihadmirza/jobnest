package com.example.jobnest.controller;

import com.example.jobnest.dto.request.ProfileUpdateRequest;
import com.example.jobnest.services.RecruiterProfilePageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for recruiter profile management.
 * Follows clean architecture - all business logic delegated to ProfileService.
 */
@Controller
@RequestMapping("/recruiter/profile")
@RequiredArgsConstructor
@SuppressWarnings("null")
public class RecruiterProfileController {

    private final RecruiterProfilePageService recruiterProfilePageService;

    @GetMapping("/edit")
    public String editProfile(Model model) {
        RecruiterProfilePageService.PageResult result = recruiterProfilePageService.editProfilePage();
        model.addAllAttributes(result.model());
        return result.viewName();
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute ProfileUpdateRequest request, Model model) {
        RecruiterProfilePageService.PageResult result = recruiterProfilePageService.updateProfile(request);
        model.addAllAttributes(result.model());
        return result.viewName();
    }
}
