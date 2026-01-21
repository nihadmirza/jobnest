package com.example.jobnest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for job information responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {

    private Integer jobId;
    private String title;
    private String description;
    private String location;
    private String city;
    private String state;
    private String country;
    private String employmentType;
    private String salary;
    private Boolean active;
    private String paymentStatus;

    // Recruiter information
    private RecruiterInfo recruiter;

    // Application information (if user is viewing the job)
    private Boolean canApply;
    private Boolean hasApplied;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecruiterInfo {
        private Integer recruiterId;
        private String firstName;
        private String lastName;
        private String company;
    }
}
