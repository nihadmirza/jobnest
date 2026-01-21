package com.example.jobnest.services.impl;

import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.PaymentTransaction;
import com.example.jobnest.entity.PricingPlan;
import com.example.jobnest.repository.JobRepository;
import com.example.jobnest.repository.PaymentTransactionRepository;
import com.example.jobnest.services.StripeService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final JobRepository jobRepository;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    private StripeService self;

    @Autowired
    @org.springframework.context.annotation.Lazy
    public void setSelf(StripeService self) {
        this.self = self;
    }

    @Override
    public String createCheckoutSession(Job job, PricingPlan plan, String successUrl, String cancelUrl)
            throws StripeException {
        // Calculate amount in cents for Stripe (e.g., $9.99 -> 999 cents)
        long amountInCents = plan.getPrice().multiply(new BigDecimal("100")).longValue();

        // Build Stripe Checkout Session parameters
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(plan.getPlanName() + " Job Post - "
                                                                        + job.getTitle())
                                                                .setDescription("Post your job for "
                                                                        + plan.getDurationDays() + " days")
                                                                .build())
                                                .build())
                                .build())
                .putMetadata("job_id", String.valueOf(job.getJobId()))
                .putMetadata("plan_id", String.valueOf(plan.getPlanId()))
                .putMetadata("recruiter_id", String.valueOf(job.getRecruiter().getUserAccountId()))
                .build();

        // Create the session
        Session session = Session.create(params);

        // Create payment transaction record
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setJob(job);
        transaction.setRecruiter(job.getRecruiter());
        transaction.setPlan(plan);
        transaction.setAmount(plan.getPrice());
        transaction.setStripeSessionId(session.getId());
        transaction.setPaymentStatus("PENDING");
        paymentTransactionRepository.save(transaction);

        return session.getUrl(); // Return the Stripe Checkout URL
    }

    @Override
    @Transactional
    public boolean handleWebhookEvent(String payload, String sigHeader) throws StripeException {
        Event event;

        try {
            // Verify webhook signature
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw e;
        } catch (Exception e) {
            // Re-throw as StripeException if possible, or wrap it
            // Since SignatureVerificationException is a StripeException, we are good.
            // But Webhook.constructEvent can throw JsonSyntaxException which is a
            // RuntimeException.
            throw new SignatureVerificationException("Invalid payload: " + e.getMessage(), null);
        }

        // Handle the checkout.session.completed event
        if ("checkout.session.completed".equals(event.getType())) {
            // dataObjectDeserializer can fail, but here we just need to get the object
            // If it fails, we can throw an exception
            Optional<com.stripe.model.StripeObject> validObject = event.getDataObjectDeserializer().getObject();

            if (validObject.isEmpty()) {
                throw new SignatureVerificationException("Unable to deserialize event data",
                        "checkout.session.completed");
            }

            Session session = (Session) validObject.get();

            self.confirmPayment(session.getId());
            return true;
        }

        return false;
    }

    @Override
    @Transactional
    public void confirmPayment(String sessionId) throws StripeException {
        // Retrieve the session from Stripe
        Session session = Session.retrieve(sessionId);

        // Find the transaction in our database
        Optional<PaymentTransaction> transactionOpt = paymentTransactionRepository.findByStripeSessionId(sessionId);

        if (transactionOpt.isEmpty()) {
            throw new com.example.jobnest.exception.ResourceNotFoundException(
                    "Transaction not found for session: " + sessionId);
        }

        PaymentTransaction transaction = transactionOpt.get();

        // Update transaction status
        transaction.setPaymentStatus("SUCCESS");
        transaction.setStripePaymentId(session.getPaymentIntent());
        paymentTransactionRepository.save(transaction);

        // Update job status
        Job job = transaction.getJob();
        if (job != null) {
            job.setPaymentStatus("PAID");
            job.setActive(true);
            job.setPlan(transaction.getPlan());

            // Set featured/premium flags based on plan
            PricingPlan plan = transaction.getPlan();
            if (plan.isPremium()) {
                job.setPremium(true);
                job.setFeatured(true);
            } else if (plan.isFeatured()) {
                job.setFeatured(true);
                job.setPremium(false);
            } else {
                job.setFeatured(false);
                job.setPremium(false);
            }

            jobRepository.save(job);
        }
    }
}
