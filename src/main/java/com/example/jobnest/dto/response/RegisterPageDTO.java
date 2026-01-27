package com.example.jobnest.dto.response;

import com.example.jobnest.dto.request.UserRegistrationRequest;
import com.example.jobnest.entity.UsersType;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Single view-model + form backing object for registration page.
 */
@Data
@Builder
public class RegisterPageDTO {
    @Valid
    private UserRegistrationRequest request;
    private List<UsersType> types;
    private String error;
    private String successMessage;
}

