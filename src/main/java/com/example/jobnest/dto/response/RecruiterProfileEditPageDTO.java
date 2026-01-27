package com.example.jobnest.dto.response;

import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.entity.Users;
import lombok.Builder;
import lombok.Data;

/**
 * Single view-model for recruiter profile edit page.
 */
@Data
@Builder
public class RecruiterProfileEditPageDTO {
    private Users user;
    private RecruiterProfile profile;
    private String error;
}

