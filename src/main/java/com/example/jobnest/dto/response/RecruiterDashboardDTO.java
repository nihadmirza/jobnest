package com.example.jobnest.dto.response;

import com.example.jobnest.entity.RecruiterProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for recruiter dashboard data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterDashboardDTO {

    private RecruiterProfile profile;
    private long applicationCount;
    private long pendingApplicationCount;
}
