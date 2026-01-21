package com.example.jobnest.services;

import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.PricingPlan;

import com.stripe.exception.StripeException;

/**
 * Service for handling payment operations.
 */
public interface PaymentService {

    /**
     * Create a Stripe checkout session for a job posting.
     *
     * @param jobId  Job ID
     * @param planId Pricing plan ID
     * @param userId User ID
     * @return Stripe checkout URL
     */
    String createCheckoutSession(int jobId, int planId, int userId) throws StripeException;

    /**
     * Process successful payment and activate job.
     *
     * @param sessionId Stripe session ID
     */
    void processSuccessfulPayment(String sessionId) throws StripeException;

    /**
     * Handle payment cancellation.
     *
     * @param jobId Job ID
     */
    void handlePaymentCancellation(int jobId);

    /**
     * Get all available pricing plans.
     *
     * @return List of pricing plans
     */
    java.util.List<PricingPlan> getAllPricingPlans();

    /**
     * Get job by ID.
     *
     * @param jobId Job ID
     * @return Job
     */
    Job getJobForPayment(int jobId);
}
