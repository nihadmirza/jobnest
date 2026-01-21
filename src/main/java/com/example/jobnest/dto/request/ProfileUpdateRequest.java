package com.example.jobnest.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * DTO for profile update requests.
 * Supports both job seeker and recruiter profile updates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Size(max = 100, message = "City name must not exceed 100 characters")
    private String city;

    @Size(max = 100, message = "State name must not exceed 100 characters")
    private String state;

    @Size(max = 100, message = "Country name must not exceed 100 characters")
    private String country;

    // Job Seeker specific fields
    private String workAuthorization;
    private String employmentType;

    // File uploads
    private MultipartFile resumeFile;
    private String resumeUrl;
    private MultipartFile image;

    // Skills (for job seekers)
    private List<SkillRequest> skills;

    // Recruiter specific fields
    @Size(max = 100, message = "Company name must not exceed 100 characters")
    private String company;

    /**
     * Inner DTO for skill information.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillRequest {
        @Size(min = 1, max = 100, message = "Skill name must be between 1 and 100 characters")
        private String name;

        private String experienceLevel;
        private String yearsOfExperience;
    }
}
