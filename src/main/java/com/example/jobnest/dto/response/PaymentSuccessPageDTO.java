package com.example.jobnest.dto.response;

import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.PaymentTransaction;
import com.example.jobnest.entity.PricingPlan;
import lombok.Builder;
import lombok.Data;

/**
 * Single view-model for payment success page.
 */
@Data
@Builder
public class PaymentSuccessPageDTO {
    private PaymentTransaction transaction;
    private Job job;
    private PricingPlan plan;
}

