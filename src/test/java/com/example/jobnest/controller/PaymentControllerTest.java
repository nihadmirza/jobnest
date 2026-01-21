package com.example.jobnest.controller;

import com.example.jobnest.repository.PaymentTransactionRepository;
import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.PaymentTransaction;
import com.example.jobnest.entity.PricingPlan;
import com.example.jobnest.services.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = "stripe.publishable.key=test")
class PaymentControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private PaymentService paymentService;

        @MockBean
        private PaymentTransactionRepository paymentTransactionRepository;

        @Test
        void testCreateCheckoutSession_Success() throws Exception {
                when(paymentService.createCheckoutSession(anyInt(), anyInt(), anyInt()))
                                .thenReturn("http://checkout-url");

                mockMvc.perform(post("/recruiter/checkout")
                                .param("jobId", "1")
                                .param("planId", "1"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("http://checkout-url"));
        }

        @Test
        void testCreateCheckoutSession_Failure() throws Exception {
                when(paymentService.createCheckoutSession(anyInt(), anyInt(), anyInt()))
                                .thenThrow(new RuntimeException("Stripe error"));

                mockMvc.perform(post("/recruiter/checkout")
                                .param("jobId", "999")
                                .param("planId", "1"))
                                .andExpect(status().isFound()) // Redirects back
                                .andExpect(redirectedUrl("/recruiter/pricing?jobId=999"));
        }

        @Test
        void testShowPricingPlans() throws Exception {
                Job job = new Job();
                job.setJobId(1);
                job.setTitle("Backend Engineer");
                job.setLocation("Baku");
                job.setEmploymentType("Full-time");
                PricingPlan plan = new PricingPlan();
                plan.setPlanId(1);
                plan.setPlanName("Basic");
                plan.setPrice(new BigDecimal("9.99"));
                plan.setDurationDays(30);

                when(paymentService.getJobForPayment(1)).thenReturn(job);
                when(paymentService.getAllPricingPlans()).thenReturn(List.of(plan));

                mockMvc.perform(get("/recruiter/pricing").param("jobId", "1"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("pricing-plans"))
                                .andExpect(model().attribute("job", job))
                                .andExpect(model().attribute("plans", List.of(plan)))
                                .andExpect(model().attributeExists("publishableKey"));
        }

        @Test
        void testPaymentSuccess_ReturnsSuccessView() throws Exception {
                Job job = new Job();
                job.setTitle("Backend Engineer");
                PricingPlan plan = new PricingPlan();
                plan.setPlanName("Basic");
                plan.setDurationDays(30);
                PaymentTransaction transaction = new PaymentTransaction();
                transaction.setJob(job);
                transaction.setPlan(plan);
                transaction.setAmount(new BigDecimal("9.99"));

                doNothing().when(paymentService).processSuccessfulPayment("sess_123");
                when(paymentTransactionRepository.findByStripeSessionId("sess_123"))
                                .thenReturn(Optional.of(transaction));

                mockMvc.perform(get("/payment/success").param("session_id", "sess_123"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("payment-success"))
                                .andExpect(model().attribute("transaction", transaction))
                                .andExpect(model().attribute("job", job))
                                .andExpect(model().attribute("plan", plan));
        }

        @Test
        void testPaymentSuccess_ReturnsCancelOnError() throws Exception {
                doNothing().when(paymentService).processSuccessfulPayment("missing");
                when(paymentTransactionRepository.findByStripeSessionId("missing"))
                                .thenReturn(Optional.empty());

                mockMvc.perform(get("/payment/success").param("session_id", "missing"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("payment-cancel"))
                                .andExpect(model().attributeExists("error"));
        }

        @Test
        void testPaymentCancel_WithJobId() throws Exception {
                mockMvc.perform(get("/payment/cancel").param("jobId", "22"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("payment-cancel"))
                                .andExpect(model().attribute("jobId", 22));

                verify(paymentService).handlePaymentCancellation(22);
        }

        @Test
        void testPaymentCancel_WithoutJobId() throws Exception {
                mockMvc.perform(get("/payment/cancel"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("payment-cancel"));
        }

        @Test
        void testStripeWebhook_ReturnsSuccess() throws Exception {
                mockMvc.perform(post("/webhook/stripe")
                                .contentType("application/json")
                                .content("{}")
                                .header("Stripe-Signature", "sig"))
                                .andExpect(status().isOk())
                                .andExpect(content().string("success"));
        }
}
