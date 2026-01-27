package com.example.jobnest.services.impl;

import com.example.jobnest.dto.response.PaymentCancelPageDTO;
import com.example.jobnest.dto.response.PaymentSuccessPageDTO;
import com.example.jobnest.dto.response.PricingPlansPageDTO;
import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.PaymentTransaction;
import com.example.jobnest.entity.PricingPlan;
import com.example.jobnest.exception.PaymentProcessingException;
import com.example.jobnest.exception.RedirectWithFlashException;
import com.example.jobnest.exception.StripeWebhookException;
import com.example.jobnest.repository.PaymentTransactionRepository;
import com.example.jobnest.services.PaymentPageService;
import com.example.jobnest.services.PaymentService;
import com.example.jobnest.services.StripeService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentPageServiceImpl implements PaymentPageService {

    private final PaymentService paymentService;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final StripeService stripeService;

    @Value("${stripe.publishable.key:}")
    private String publishableKey;

    @Override
    @Transactional(readOnly = true)
    public PageResult showPricingPlans(int jobId) {
        Job job = paymentService.getJobForPayment(jobId);
        List<PricingPlan> plans = paymentService.getAllPricingPlans();

        PricingPlansPageDTO page = PricingPlansPageDTO.builder()
                .job(job)
                .plans(plans)
                .publishableKey(publishableKey)
                .build();

        return new PageResult("pricing-plans", Map.of("page", page));
    }

    @Override
    @Transactional
    public RedirectResult createCheckoutSession(int jobId, int planId) {
        try {
            String checkoutUrl = paymentService.createCheckoutSession(jobId, planId, 0);
            return new RedirectResult("redirect:" + checkoutUrl);
        } catch (Exception e) {
            throw new RedirectWithFlashException(
                    "redirect:/recruiter/pricing?jobId=" + jobId,
                    "error",
                    "Error initiating payment: " + e.getMessage()
            );
        }
    }

    @Override
    @Transactional
    public PageResult paymentSuccess(String sessionId) {
        try {
            paymentService.processSuccessfulPayment(sessionId);
        } catch (Exception e) {
            throw new PaymentProcessingException("Error processing payment confirmation: " + e.getMessage());
        }

        PaymentTransaction transaction = paymentTransactionRepository.findByStripeSessionId(sessionId)
                .orElseThrow(() -> new PaymentProcessingException("Transaction not found"));

        PaymentSuccessPageDTO page = PaymentSuccessPageDTO.builder()
                .transaction(transaction)
                .job(transaction.getJob())
                .plan(transaction.getPlan())
                .build();

        return new PageResult("payment-success", Map.of("page", page));
    }

    @Override
    @Transactional
    public PageResult paymentCancel(Integer jobId) {
        if (jobId != null) {
            paymentService.handlePaymentCancellation(jobId);
        }
        PaymentCancelPageDTO page = PaymentCancelPageDTO.builder().jobId(jobId).build();
        return new PageResult("payment-cancel", Map.of("page", page));
    }

    @Override
    public String handleStripeWebhook(String payload, String sigHeader) {
        try {
            stripeService.handleWebhookEvent(payload, sigHeader);
            return "success";
        } catch (StripeException e) {
            throw new StripeWebhookException("error: " + e.getMessage(), e);
        }
    }
}

