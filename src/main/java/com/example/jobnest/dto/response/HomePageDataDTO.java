package com.example.jobnest.dto.response;

import com.example.jobnest.entity.Job;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for homepage data (featured jobs and statistics).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomePageDataDTO {

    private List<Job> featuredJobs;
    private long totalJobs;
    private long totalRecruiters;
    private long totalApplications;
}
