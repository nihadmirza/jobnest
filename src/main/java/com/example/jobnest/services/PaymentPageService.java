package com.example.jobnest.services;

import java.util.Map;

/**
 * Facade service for payment-related MVC pages.
 */
public interface PaymentPageService {

    record PageResult(String viewName, Map<String, Object> model) {}

    record RedirectResult(String redirectTo) {}

    PageResult showPricingPlans(int jobId);

    RedirectResult createCheckoutSession(int jobId, int planId);

    PageResult paymentSuccess(String sessionId);

    PageResult paymentCancel(Integer jobId);

    String handleStripeWebhook(String payload, String sigHeader);
}

