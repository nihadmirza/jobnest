package com.example.jobnest.controller;

import com.example.jobnest.dto.response.PaymentCancelPageDTO;
import com.example.jobnest.dto.response.PaymentSuccessPageDTO;
import com.example.jobnest.dto.response.PricingPlansPageDTO;
import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.PaymentTransaction;
import com.example.jobnest.entity.PricingPlan;
import com.example.jobnest.services.PaymentPageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private PaymentPageService paymentPageService;

        @Test
        void testCreateCheckoutSession_Success() throws Exception {
                when(paymentPageService.createCheckoutSession(anyInt(), anyInt()))
                                .thenReturn(new PaymentPageService.RedirectResult("redirect:http://checkout-url"));

                mockMvc.perform(post("/recruiter/checkout")
                                .param("jobId", "1")
                                .param("planId", "1"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("http://checkout-url"));
        }

        @Test
        void testCreateCheckoutSession_Failure() throws Exception {
                when(paymentPageService.createCheckoutSession(anyInt(), anyInt()))
                                .thenReturn(new PaymentPageService.RedirectResult("redirect:/recruiter/pricing?jobId=999"));

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

                PricingPlansPageDTO page = PricingPlansPageDTO.builder()
                        .job(job)
                        .plans(List.of(plan))
                        .publishableKey("test")
                        .build();
                when(paymentPageService.showPricingPlans(1))
                        .thenReturn(new PaymentPageService.PageResult("pricing-plans", Map.of("page", page)));

                mockMvc.perform(get("/recruiter/pricing").param("jobId", "1"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("pricing-plans"))
                                .andExpect(model().attributeExists("page"));
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

                PaymentSuccessPageDTO page = PaymentSuccessPageDTO.builder()
                        .transaction(transaction)
                        .job(job)
                        .plan(plan)
                        .build();
                when(paymentPageService.paymentSuccess("sess_123"))
                        .thenReturn(new PaymentPageService.PageResult("payment-success", Map.of("page", page)));

                mockMvc.perform(get("/payment/success").param("session_id", "sess_123"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("payment-success"))
                                .andExpect(model().attributeExists("page"));
        }

        @Test
        void testPaymentSuccess_ReturnsCancelOnError() throws Exception {
                when(paymentPageService.paymentSuccess("missing"))
                        .thenReturn(new PaymentPageService.PageResult("payment-cancel", Map.of("error", "boom")));

                mockMvc.perform(get("/payment/success").param("session_id", "missing"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("payment-cancel"))
                                .andExpect(model().attributeExists("error"));
        }

        @Test
        void testPaymentCancel_WithJobId() throws Exception {
                PaymentCancelPageDTO page = PaymentCancelPageDTO.builder().jobId(22).build();
                when(paymentPageService.paymentCancel(22))
                        .thenReturn(new PaymentPageService.PageResult("payment-cancel", Map.of("page", page)));

                mockMvc.perform(get("/payment/cancel").param("jobId", "22"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("payment-cancel"))
                                .andExpect(model().attributeExists("page"));

                verify(paymentPageService).paymentCancel(22);
        }

        @Test
        void testPaymentCancel_WithoutJobId() throws Exception {
                when(paymentPageService.paymentCancel(null))
                        .thenReturn(new PaymentPageService.PageResult("payment-cancel",
                                Map.of("page", PaymentCancelPageDTO.builder().jobId(null).build())));

                mockMvc.perform(get("/payment/cancel"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("payment-cancel"));
        }

        @Test
        void testStripeWebhook_ReturnsSuccess() throws Exception {
                when(paymentPageService.handleStripeWebhook(anyString(), anyString())).thenReturn("success");

                mockMvc.perform(post("/webhook/stripe")
                                .contentType("application/json")
                                .content("{}")
                                .header("Stripe-Signature", "sig"))
                                .andExpect(status().isOk())
                                .andExpect(content().string("success"));
        }
}
