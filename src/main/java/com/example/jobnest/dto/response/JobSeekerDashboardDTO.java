package com.example.jobnest.dto.response;

import com.example.jobnest.entity.JobSeekerApply;
import com.example.jobnest.entity.JobSeekerProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for job seeker dashboard data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSeekerDashboardDTO {

    private JobSeekerProfile profile;
    private List<JobSeekerApply> applications;
}
