package com.example.jobnest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "payment_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {

    private static final String PAYMENT_STATUS_PENDING = "PENDING";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transactionId;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    @ManyToOne
    @JoinColumn(name = "recruiter_id", nullable = false)
    private RecruiterProfile recruiter;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private PricingPlan plan;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(length = 255, unique = true)
    private String stripePaymentId;

    @Column(length = 255, unique = true)
    private String stripeSessionId;

    @Column(length = 50, nullable = false)
    private String paymentStatus = PAYMENT_STATUS_PENDING;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @PrePersist
    protected void onCreate() {
        createdDate = new Date();
        if (paymentStatus == null) {
            paymentStatus = PAYMENT_STATUS_PENDING;
        }
    }

    // Helper methods
    public boolean isPending() {
        return PAYMENT_STATUS_PENDING.equalsIgnoreCase(paymentStatus);
    }

    public boolean isSuccess() {
        return "SUCCESS".equalsIgnoreCase(paymentStatus);
    }

    public boolean isFailed() {
        return "FAILED".equalsIgnoreCase(paymentStatus);
    }

    public String getFormattedAmount() {
        return String.format("$%.2f", amount);
    }
}
