package com.example.jobnest.services.impl;

import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.PaymentTransaction;
import com.example.jobnest.entity.PricingPlan;
import com.example.jobnest.entity.Users;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.exception.ResourceNotFoundException;
import com.example.jobnest.repository.JobRepository;
import com.example.jobnest.repository.PaymentTransactionRepository;
import com.example.jobnest.services.StripeService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripeServiceImplTest {

    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private StripeServiceImpl stripeService;

    private Job mockJob;
    private PricingPlan mockPlan;
    private Users mockRecruiter;

    @BeforeEach
    void setUp() {
        mockRecruiter = new Users();
        mockRecruiter.setUserId(1);

        RecruiterProfile rp = new RecruiterProfile(mockRecruiter);
        rp.setUserAccountId(100); // Changed to int matching Entity

        mockJob = new Job();
        mockJob.setJobId(1); // int
        mockJob.setTitle("Software Engineer");
        mockJob.setRecruiter(rp);

        mockPlan = new PricingPlan();
        mockPlan.setPlanId(1);
        mockPlan.setPlanName("Basic");
        mockPlan.setPrice(new BigDecimal("9.99"));
        mockPlan.setDurationDays(30);

        ReflectionTestUtils.setField(stripeService, "webhookSecret", "whsec_test");
    }

    @Test
    void testCreateCheckoutSession() throws Exception {
        // Mock Stripe Session.create static method
        try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
            Session mockSession = mock(Session.class);
            when(mockSession.getId()).thenReturn("sess_123");
            when(mockSession.getUrl()).thenReturn("http://checkout-url");

            mockedSession.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenReturn(mockSession);

            String url = stripeService.createCheckoutSession(mockJob, mockPlan, "http://success", "http://cancel");

            assertNotNull(url);
            assertEquals("http://checkout-url", url);
            verify(paymentTransactionRepository, times(1)).save(any());
        }
    }

    @Test
    void handleWebhookEvent_returnsTrueAndConfirmsPayment() throws Exception {
        StripeService self = mock(StripeService.class);
        stripeService.setSelf(self);

        Event event = mock(Event.class);
        when(event.getType()).thenReturn("checkout.session.completed");
        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);

        Session session = mock(Session.class);
        when(session.getId()).thenReturn("sess_123");
        when(deserializer.getObject()).thenReturn(Optional.of(session));

        try (MockedStatic<Webhook> mockedWebhook = mockStatic(Webhook.class)) {
            mockedWebhook.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString()))
                    .thenReturn(event);

            boolean result = stripeService.handleWebhookEvent("payload", "sig");

            assertTrue(result);
            verify(self).confirmPayment("sess_123");
        }
    }

    @Test
    void handleWebhookEvent_returnsFalseForOtherEvents() throws Exception {
        StripeService self = mock(StripeService.class);
        stripeService.setSelf(self);

        Event event = mock(Event.class);
        when(event.getType()).thenReturn("charge.failed");

        try (MockedStatic<Webhook> mockedWebhook = mockStatic(Webhook.class)) {
            mockedWebhook.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString()))
                    .thenReturn(event);

            boolean result = stripeService.handleWebhookEvent("payload", "sig");

            assertFalse(result);
            verifyNoInteractions(self);
        }
    }

    @Test
    void handleWebhookEvent_throwsWhenDataObjectMissing() throws Exception {
        Event event = mock(Event.class);
        when(event.getType()).thenReturn("checkout.session.completed");
        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);
        when(deserializer.getObject()).thenReturn(Optional.empty());

        try (MockedStatic<Webhook> mockedWebhook = mockStatic(Webhook.class)) {
            mockedWebhook.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString()))
                    .thenReturn(event);

            assertThrows(SignatureVerificationException.class,
                    () -> stripeService.handleWebhookEvent("payload", "sig"));
        }
    }

    @Test
    void confirmPayment_updatesTransactionAndJobForPremiumPlan() throws Exception {
        PricingPlan premium = new PricingPlan();
        premium.setPlanName("Premium");

        Job job = new Job();
        job.setJobId(10);

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setJob(job);
        transaction.setPlan(premium);

        when(paymentTransactionRepository.findByStripeSessionId("sess_123"))
                .thenReturn(Optional.of(transaction));

        Session session = mock(Session.class);
        when(session.getPaymentIntent()).thenReturn("pi_123");

        try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
            mockedSession.when(() -> Session.retrieve("sess_123")).thenReturn(session);

            stripeService.confirmPayment("sess_123");
        }

        assertEquals("SUCCESS", transaction.getPaymentStatus());
        assertEquals("pi_123", transaction.getStripePaymentId());
        assertEquals("PAID", job.getPaymentStatus());
        assertTrue(job.isActive());
        assertTrue(job.isPremium());
        assertTrue(job.isFeatured());
        assertEquals(premium, job.getPlan());

        verify(paymentTransactionRepository).save(transaction);
        verify(jobRepository).save(job);
    }

    @Test
    void confirmPayment_throwsWhenTransactionMissing() {
        when(paymentTransactionRepository.findByStripeSessionId("missing"))
                .thenReturn(Optional.empty());

        try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
            mockedSession.when(() -> Session.retrieve("missing")).thenReturn(mock(Session.class));

            assertThrows(ResourceNotFoundException.class,
                    () -> stripeService.confirmPayment("missing"));
        }
    }
}
