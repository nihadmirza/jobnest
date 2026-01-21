package com.example.jobnest.controller;

import com.example.jobnest.dto.request.ProfileUpdateRequest;
import com.example.jobnest.entity.JobSeekerProfile;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.entity.Users;
import com.example.jobnest.services.AuthenticationService;
import com.example.jobnest.services.ProfileService;
import com.example.jobnest.services.UsersService;
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
public class ProfileController {

    private static final String ERROR_ATTR = "error";
    private static final String PROFILE_ATTR = "profile";
    private static final String USER_ATTR = "user";
    private static final String LOGIN_ERROR_REDIRECT = "redirect:/login?error=true";

    private final UsersService usersService;
    private final ProfileService profileService;
    private final AuthenticationService authenticationService;

    @GetMapping("/profile/edit")
    public String editProfile(Model model) {
        Users user = authenticationService.getCurrentAuthenticatedUser();
        if (user == null) {
            return LOGIN_ERROR_REDIRECT;
        }

        if (user.getUserTypeId() == null) {
            model.addAttribute(ERROR_ATTR, "İstifadəçi tipi tapılmadı");
            return LOGIN_ERROR_REDIRECT;
        }

        int userTypeId = user.getUserTypeId().getUserTypeId();

        if (userTypeId == 1) {
            // Recruiter - for now redirect to dashboard
            return "redirect:/dashboard";
        }

        // Job Seeker
        JobSeekerProfile profile = profileService.getJobSeekerProfile(user.getUserId());
        model.addAttribute(PROFILE_ATTR, profile);
        model.addAttribute(USER_ATTR, user);
        return "profile-edit";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute ProfileUpdateRequest request, Model model) {
        Users user = authenticationService.getCurrentAuthenticatedUser();
        if (user == null) {
            return LOGIN_ERROR_REDIRECT;
        }

        try {
            int userTypeId = user.getUserTypeId().getUserTypeId();

            if (userTypeId == 1) {
                // Update recruiter profile
                profileService.updateRecruiterProfile(request, user.getUserId());
            } else {
                // Convert skills arrays to SkillRequest list if provided
                if (request.getSkills() == null) {
                    request.setSkills(java.util.Collections.emptyList());
                }

                // Update job seeker profile
                profileService.updateJobSeekerProfile(request, user.getUserId());
            }

            return "redirect:/dashboard?success=true";
        } catch (Exception e) {
            model.addAttribute(ERROR_ATTR, e.getMessage());

            // Reload profile and show form again
            if (user.getUserTypeId().getUserTypeId() == 1) {
                RecruiterProfile profile = profileService.getRecruiterProfile(user.getUserId());
                model.addAttribute(PROFILE_ATTR, profile);
            } else {
                JobSeekerProfile profile = profileService.getJobSeekerProfile(user.getUserId());
                model.addAttribute(PROFILE_ATTR, profile);
            }

            model.addAttribute(USER_ATTR, user);
            return "profile-edit";
        }
    }
}
