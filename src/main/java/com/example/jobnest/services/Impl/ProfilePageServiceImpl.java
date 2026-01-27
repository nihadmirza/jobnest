package com.example.jobnest.services.impl;

import com.example.jobnest.common.UserType;
import com.example.jobnest.dto.request.ProfileUpdateRequest;
import com.example.jobnest.dto.response.ProfileEditPageDTO;
import com.example.jobnest.entity.JobSeekerProfile;
import com.example.jobnest.entity.Users;
import com.example.jobnest.exception.ValidationException;
import com.example.jobnest.services.AuthenticationService;
import com.example.jobnest.services.ProfilePageService;
import com.example.jobnest.services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * MVC facade for profile pages.
 * Contains all branching/decisions so controllers stay thin.
 */
@Service
@RequiredArgsConstructor
public class ProfilePageServiceImpl implements ProfilePageService {

    private static final String ERROR_ATTR = "error";
    private static final String LOGIN_ERROR_REDIRECT = "redirect:/login?error=true";
    private static final String DASHBOARD_REDIRECT = "redirect:/dashboard";
    private static final String DASHBOARD_SUCCESS_REDIRECT = "redirect:/dashboard?success=true";
    private static final String PROFILE_EDIT_VIEW = "profile-edit";

    private final AuthenticationService authenticationService;
    private final ProfileService profileService;

    @Override
    public PageResult editProfilePage() {
        Users user = authenticationService.getCurrentAuthenticatedUser();
        if (user == null) {
            return new PageResult(LOGIN_ERROR_REDIRECT, Map.of());
        }
        if (user.getUserTypeId() == null) {
            return new PageResult(LOGIN_ERROR_REDIRECT, Map.of(ERROR_ATTR, "İstifadəçi tipi tapılmadı"));
        }

        UserType userType = UserType.fromUsersType(user.getUserTypeId())
                .orElseThrow(() -> new ValidationException("İstifadəçi tipi tapılmadı"));
        if (userType == UserType.RECRUITER) {
            return new PageResult(DASHBOARD_REDIRECT, Map.of());
        }

        JobSeekerProfile profile = profileService.getJobSeekerProfile(user.getUserId());
        ProfileEditPageDTO page = ProfileEditPageDTO.builder()
                .user(user)
                .profile(profile)
                .build();
        return new PageResult(PROFILE_EDIT_VIEW, Map.of("page", page));
    }

    @Override
    public PageResult updateProfile(ProfileUpdateRequest request) {
        Users user = authenticationService.getCurrentAuthenticatedUser();
        if (user == null) {
            return new PageResult(LOGIN_ERROR_REDIRECT, Map.of());
        }

        UserType userType = UserType.fromUsersType(user.getUserTypeId()).orElse(null);

        try {
            if (userType == UserType.RECRUITER) {
                profileService.updateRecruiterProfile(request, user.getUserId());
            } else {
                // Job seeker update; service already handles null skills safely.
                profileService.updateJobSeekerProfile(request, user.getUserId());
            }
            return new PageResult(DASHBOARD_SUCCESS_REDIRECT, Map.of());
        } catch (Exception e) {
            // Preserve current UX: stay on form with error message + reloaded profile.
            if (userType == UserType.RECRUITER) {
                // Recruiters shouldn't hit this page; keep app safe by redirecting to dashboard.
                return new PageResult(DASHBOARD_REDIRECT, Map.of());
            } else {
                JobSeekerProfile profile = profileService.getJobSeekerProfile(user.getUserId());
                Map<String, Object> model = new HashMap<>();
                model.put("page", ProfileEditPageDTO.builder()
                        .user(user)
                        .profile(profile)
                        .error(e.getMessage())
                        .build());
                return new PageResult(PROFILE_EDIT_VIEW, model);
            }
        }
    }
}

