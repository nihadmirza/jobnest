package com.example.jobnest.dto.response;

import com.example.jobnest.common.UserType;
import com.example.jobnest.entity.JobSeekerApply;
import com.example.jobnest.entity.JobSeekerProfile;
import com.example.jobnest.entity.RecruiterProfile;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Unified dashboard view-model returned by DashboardService.
 * Template consumes ONLY this object via model attribute "dashboardData".
 */
@Data
@Builder
public class UnifiedDashboardDTO {
    private UserType userType;

    // Either recruiterProfile or jobSeekerProfile will be set depending on userType
    private RecruiterProfile recruiterProfile;
    private JobSeekerProfile jobSeekerProfile;

    // Recruiter stats
    private Long applicationCount;
    private Long pendingApplicationCount;

    // Job seeker data
    private List<JobSeekerApply> applications;
}

