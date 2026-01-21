package com.example.jobnest.services.impl;

import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.PricingPlan;
import com.example.jobnest.exception.ResourceNotFoundException;
import com.example.jobnest.repository.JobRepository;
import com.example.jobnest.repository.PricingPlanRepository;
import com.example.jobnest.services.PaymentService;
import com.example.jobnest.services.StripeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stripe.exception.StripeException;

import java.util.List;

/**
 * Implementation of PaymentService.
 * Handles payment business logic.
 */
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final JobRepository jobRepository;
    private final PricingPlanRepository pricingPlanRepository;
    private final StripeService stripeService;

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    @Override
    public String createCheckoutSession(int jobId, int planId, int userId) throws StripeException {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", jobId));

        PricingPlan plan = pricingPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Pricing Plan", planId));

        // Create Stripe checkout session
        return stripeService.createCheckoutSession(job, plan, successUrl, cancelUrl);
    }

    @Override
    @Transactional
    public void processSuccessfulPayment(String sessionId) throws StripeException {
        stripeService.confirmPayment(sessionId);
    }

    @Override
    @Transactional
    public void handlePaymentCancellation(int jobId) {
        // Payment cancellation logic can be added here if needed
        // For now, the job remains in PENDING status
    }

    @Override
    public List<PricingPlan> getAllPricingPlans() {
        return pricingPlanRepository.findAll();
    }

    @Override
    public Job getJobForPayment(int jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", jobId));
    }
}
