package com.example.jobnest.controller;

import com.example.jobnest.dto.request.ProfileUpdateRequest;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.entity.Users;
import com.example.jobnest.services.AuthenticationService;
import com.example.jobnest.services.ProfileService;
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
public class RecruiterProfileController {

    private final ProfileService profileService;
    private final AuthenticationService authenticationService;

    @GetMapping("/edit")
    public String editProfile(Model model) {
        Users user = authenticationService.getCurrentAuthenticatedUser();
        RecruiterProfile profile = profileService.getRecruiterProfile(user.getUserId());

        model.addAttribute("profile", profile);
        model.addAttribute("user", user);
        return "recruiter-profile-edit";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute ProfileUpdateRequest request, Model model) {
        Users user = authenticationService.getCurrentAuthenticatedUser();

        try {
            profileService.updateRecruiterProfile(request, user.getUserId());
            return "redirect:/dashboard?success=true";
        } catch (Exception e) {
            RecruiterProfile profile = profileService.getRecruiterProfile(user.getUserId());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("profile", profile);
            return "recruiter-profile-edit";
        }
    }
}
