package com.example.jobnest.services;

import com.example.jobnest.dto.request.ProfileUpdateRequest;

import java.util.Map;

/**
 * Facade for profile pages (MVC-friendly).
 * Controller should call exactly one method and render returned view + model.
 */
public interface ProfilePageService {

    PageResult editProfilePage();

    PageResult updateProfile(ProfileUpdateRequest request);

    record PageResult(String viewName, Map<String, ?> model) {
    }
}

