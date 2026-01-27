package com.example.jobnest.services;

import com.example.jobnest.dto.request.ProfileUpdateRequest;

import java.util.Map;

/**
 * Facade for recruiter profile pages (MVC-friendly).
 */
public interface RecruiterProfilePageService {

    PageResult editProfilePage();

    PageResult updateProfile(ProfileUpdateRequest request);

    record PageResult(String viewName, Map<String, ?> model) {}
}

