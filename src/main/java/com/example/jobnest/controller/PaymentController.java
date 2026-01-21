package com.example.jobnest.controller;

import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.PaymentTransaction;
import com.example.jobnest.entity.PricingPlan;
import com.example.jobnest.repository.PaymentTransactionRepository;
import com.example.jobnest.services.PaymentService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Value("${stripe.publishable.key}")
    private String publishableKey;

    @GetMapping("/recruiter/pricing")
    public String showPricingPlans(@RequestParam("jobId") int jobId, Model model) {
        // Fetch the job via service
        Job job = paymentService.getJobForPayment(jobId);

        // Fetch ALL pricing plans from database via service
        List<PricingPlan> plans = paymentService.getAllPricingPlans();

        // Add to model for Thymeleaf
        model.addAttribute("job", job);
        model.addAttribute("plans", plans);
        model.addAttribute("publishableKey", publishableKey);

        return "pricing-plans";
    }

    @PostMapping("/recruiter/checkout")
    public String createCheckoutSession(
            @RequestParam("jobId") int jobId,
            @RequestParam("planId") int planId,
            RedirectAttributes redirectAttributes) {

        try {
            // Delegate to service
            String checkoutUrl = paymentService.createCheckoutSession(jobId, planId, 0);

            // Redirect to Stripe checkout page
            return "redirect:" + checkoutUrl;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error initiating payment: " + e.getMessage());
            return "redirect:/recruiter/pricing?jobId=" + jobId;
        }
    }

    @GetMapping("/payment/success")
    public String paymentSuccess(@RequestParam("session_id") String sessionId, Model model) {
        try {
            // Confirm the payment via service
            paymentService.processSuccessfulPayment(sessionId);

            // Get transaction details
            PaymentTransaction transaction = paymentTransactionRepository.findByStripeSessionId(sessionId)
                    .orElseThrow(() -> new RuntimeException("Transaction not found"));

            model.addAttribute("transaction", transaction);
            model.addAttribute("job", transaction.getJob());
            model.addAttribute("plan", transaction.getPlan());

            return "payment-success";

        } catch (Exception e) {
            model.addAttribute("error", "Error processing payment confirmation: " + e.getMessage());
            return "payment-cancel";
        }
    }

    @GetMapping("/payment/cancel")
    public String paymentCancel(@RequestParam(value = "jobId", required = false) Integer jobId, Model model) {
        if (jobId != null) {
            paymentService.handlePaymentCancellation(jobId);
            model.addAttribute("jobId", jobId);
        }
        return "payment-cancel";
    }

    @PostMapping("/webhook/stripe")
    @ResponseBody
    public String handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        try {
            // Webhook handling logic would go here
            // For now, returning success
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
}
