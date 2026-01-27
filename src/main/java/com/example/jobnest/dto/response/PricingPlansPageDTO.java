package com.example.jobnest.dto.response;

import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.PricingPlan;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Single view-model for pricing plans page.
 */
@Data
@Builder
public class PricingPlansPageDTO {
    private Job job;
    private List<PricingPlan> plans;
    private String publishableKey;
}

