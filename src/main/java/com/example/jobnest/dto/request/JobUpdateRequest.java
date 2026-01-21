package com.example.jobnest.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating an existing job posting.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobUpdateRequest {

    @NotBlank(message = "Job title is required")
    @Size(min = 3, max = 200, message = "Job title must be between 3 and 200 characters")
    private String title;

    @NotBlank(message = "Job description is required")
    @Size(min = 10, max = 5000, message = "Job description must be between 10 and 5000 characters")
    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Employment type is required")
    @Pattern(regexp = "FULL_TIME|PART_TIME|CONTRACT|INTERNSHIP|TEMPORARY", message = "Invalid employment type")
    private String employmentType;

    @Size(max = 100, message = "Salary information must not exceed 100 characters")
    private String salary;

    @NotNull(message = "Active status is required")
    private Boolean active;
}
