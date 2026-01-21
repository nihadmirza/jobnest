package com.example.jobnest.services.impl;

import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.PricingPlan;
import com.example.jobnest.exception.ResourceNotFoundException;
import com.example.jobnest.repository.JobRepository;
import com.example.jobnest.repository.PricingPlanRepository;
import com.example.jobnest.services.StripeService;
import com.stripe.exception.StripeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private PricingPlanRepository pricingPlanRepository;

    @Mock
    private StripeService stripeService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paymentService, "successUrl", "http://success");
        ReflectionTestUtils.setField(paymentService, "cancelUrl", "http://cancel");
    }

    @Test
    void createCheckoutSession_returnsStripeUrl() throws Exception {
        Job job = new Job();
        job.setJobId(1);
        PricingPlan plan = new PricingPlan();
        plan.setPlanId(2);

        when(jobRepository.findById(1)).thenReturn(Optional.of(job));
        when(pricingPlanRepository.findById(2)).thenReturn(Optional.of(plan));
        when(stripeService.createCheckoutSession(job, plan, "http://success", "http://cancel"))
                .thenReturn("http://checkout");

        String url = paymentService.createCheckoutSession(1, 2, 0);

        assertEquals("http://checkout", url);
    }

    @Test
    void createCheckoutSession_throwsWhenJobMissing() {
        when(jobRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentService.createCheckoutSession(1, 2, 0));
    }

    @Test
    void createCheckoutSession_throwsWhenPlanMissing() {
        Job job = new Job();
        job.setJobId(1);
        when(jobRepository.findById(1)).thenReturn(Optional.of(job));
        when(pricingPlanRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentService.createCheckoutSession(1, 2, 0));
    }

    @Test
    void processSuccessfulPayment_delegatesToStripe() throws StripeException {
        paymentService.processSuccessfulPayment("sess_123");
        verify(stripeService).confirmPayment("sess_123");
    }

    @Test
    void handlePaymentCancellation_doesNotThrow() {
        paymentService.handlePaymentCancellation(10);
    }

    @Test
    void getAllPricingPlans_returnsRepositoryData() {
        when(pricingPlanRepository.findAll()).thenReturn(List.of(new PricingPlan(), new PricingPlan()));
        assertEquals(2, paymentService.getAllPricingPlans().size());
    }

    @Test
    void getJobForPayment_returnsJob() {
        Job job = new Job();
        job.setJobId(9);
        when(jobRepository.findById(9)).thenReturn(Optional.of(job));

        Job result = paymentService.getJobForPayment(9);
        assertNotNull(result);
        assertEquals(9, result.getJobId());
    }
}
