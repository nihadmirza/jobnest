package com.example.jobnest.services.impl;

import com.example.jobnest.dto.request.ProfileUpdateRequest;
import com.example.jobnest.dto.response.RecruiterProfileEditPageDTO;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.entity.Users;
import com.example.jobnest.services.AuthenticationService;
import com.example.jobnest.services.ProfileService;
import com.example.jobnest.services.RecruiterProfilePageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecruiterProfilePageServiceImpl implements RecruiterProfilePageService {

    private static final String LOGIN_ERROR_REDIRECT = "redirect:/login?error=true";
    private static final String DASHBOARD_SUCCESS_REDIRECT = "redirect:/dashboard?success=true";
    private static final String PROFILE_EDIT_VIEW = "recruiter-profile-edit";

    private final ProfileService profileService;
    private final AuthenticationService authenticationService;

    @Override
    public PageResult editProfilePage() {
        Users user = authenticationService.getCurrentAuthenticatedUser();
        if (user == null) {
            return new PageResult(LOGIN_ERROR_REDIRECT, Map.of());
        }

        RecruiterProfile profile = profileService.getRecruiterProfile(user.getUserId());
        RecruiterProfileEditPageDTO page = RecruiterProfileEditPageDTO.builder()
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

        try {
            profileService.updateRecruiterProfile(request, user.getUserId());
            return new PageResult(DASHBOARD_SUCCESS_REDIRECT, Map.of());
        } catch (Exception e) {
            RecruiterProfile profile = profileService.getRecruiterProfile(user.getUserId());
            Map<String, Object> model = new HashMap<>();
            model.put("page", RecruiterProfileEditPageDTO.builder()
                    .user(user)
                    .profile(profile)
                    .error(e.getMessage())
                    .build());
            return new PageResult(PROFILE_EDIT_VIEW, model);
        }
    }
}

