package com.example.jobnest.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * Single view-model for recruiter applications page.
 */
@Data
@Builder
public class RecruiterApplicationsPageDTO {
    private RecruiterApplicationStatsDTO stats;
    private Integer selectedJobId;
    private String selectedStatus;
}
