package com.example.jobnest.services;

import com.example.jobnest.dto.response.RegisterPageDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * Facade for registration (MVC-friendly).
 */
public interface RegistrationPageService {

    record PageResult(String viewName, Map<String, Object> model) {}

    PageResult showRegisterPage(String role);

    PageResult showRegisterPage(RegisterPageDTO page);

    PageResult registerNewUser(RegisterPageDTO page, HttpServletRequest httpRequest);
}

