package com.example.jobnest.dto.response;

import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO for detailed admin reports with analytics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReportsDTO {

    // User statistics
    private long totalUsers;
    private long recruiters;
    private long jobSeekers;

    // Job statistics
    private long totalJobs;
    private long activeJobs;
    private long totalApplications;
    private String avgApplications;

    // Top 5 lists
    private List<Map.Entry<Job, Long>> topJobs;
    private List<Map.Entry<RecruiterProfile, Long>> topRecruiters;
    private List<Map.Entry<Users, Long>> topJobSeekers;
}
