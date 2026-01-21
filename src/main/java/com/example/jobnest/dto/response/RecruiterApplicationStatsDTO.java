package com.example.jobnest.dto.response;

import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.JobSeekerApply;
import com.example.jobnest.entity.JobSeekerProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO for recruiter applications with statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterApplicationStatsDTO {

    private long totalApplications;
    private long pendingCount;
    private long reviewedCount;
    private long acceptedCount;
    private long rejectedCount;

    private List<JobSeekerApply> applications;
    private Map<Integer, JobSeekerProfile> candidateProfilesMap;
    private List<Job> allJobs;
}
