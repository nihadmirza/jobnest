package com.example.jobnest.dto.response;

import com.example.jobnest.entity.JobSeekerProfile;
import com.example.jobnest.entity.Users;
import lombok.Builder;
import lombok.Data;

/**
 * Single view-model for job seeker profile edit page.
 */
@Data
@Builder
public class ProfileEditPageDTO {
    private Users user;
    private JobSeekerProfile profile;
    private String error;
}

