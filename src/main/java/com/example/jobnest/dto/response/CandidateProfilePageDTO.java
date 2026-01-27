package com.example.jobnest.dto.response;

import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.JobSeekerApply;
import com.example.jobnest.entity.JobSeekerProfile;
import com.example.jobnest.entity.Users;
import lombok.Builder;
import lombok.Data;

/**
 * Single view-model for candidate profile page.
 */
@Data
@Builder
public class CandidateProfilePageDTO {
    private JobSeekerApply application;
    private JobSeekerProfile candidateProfile;
    private Users candidateUser;
    private Job job;
}
