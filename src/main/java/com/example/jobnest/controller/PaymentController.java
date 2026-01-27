package com.example.jobnest.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.jobnest.services.PaymentPageService;

@Controller
@RequiredArgsConstructor
@SuppressWarnings("null")
public class PaymentController {

    private final PaymentPageService paymentPageService;

    @GetMapping("/recruiter/pricing")
    public String showPricingPlans(@RequestParam("jobId") int jobId, Model model) {
        PaymentPageService.PageResult result = paymentPageService.showPricingPlans(jobId);
        model.addAllAttributes(result.model());
        return result.viewName();
    }

    @PostMapping("/recruiter/checkout")
    public String createCheckoutSession(@RequestParam("jobId") int jobId, @RequestParam("planId") int planId) {
        return paymentPageService.createCheckoutSession(jobId, planId).redirectTo();
    }

    @GetMapping("/payment/success")
    public String paymentSuccess(@RequestParam("session_id") String sessionId, Model model) {
        PaymentPageService.PageResult result = paymentPageService.paymentSuccess(sessionId);
        model.addAllAttributes(result.model());
        return result.viewName();
    }

    @GetMapping("/payment/cancel")
    public String paymentCancel(@RequestParam(value = "jobId", required = false) Integer jobId, Model model) {
        PaymentPageService.PageResult result = paymentPageService.paymentCancel(jobId);
        model.addAllAttributes(result.model());
        return result.viewName();
    }

    @PostMapping("/webhook/stripe")
    @ResponseBody
    public String handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        return paymentPageService.handleStripeWebhook(payload, sigHeader);
    }
}
