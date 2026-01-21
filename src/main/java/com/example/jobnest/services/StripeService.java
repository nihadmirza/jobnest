package com.example.jobnest.services;

import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.PricingPlan;

import com.stripe.exception.StripeException;

public interface StripeService {

    /**
     * Creates a Stripe Checkout Session for job payment
     * 
     * @param job        The job being posted
     * @param plan       The selected pricing plan
     * @param successUrl URL to redirect after successful payment
     * @param cancelUrl  URL to redirect if payment is cancelled
     * @return Stripe Checkout Session ID
     */
    String createCheckoutSession(Job job, PricingPlan plan, String successUrl, String cancelUrl) throws StripeException;

    /**
     * Handles incoming Stripe webhook events
     * 
     * @param payload   The webhook payload
     * @param sigHeader The Stripe signature header
     * @return true if event was handled successfully
     */
    boolean handleWebhookEvent(String payload, String sigHeader) throws StripeException;

    /**
     * Confirms payment and activates the job
     * 
     * @param sessionId Stripe Checkout Session ID
     */
    void confirmPayment(String sessionId) throws StripeException;
}
