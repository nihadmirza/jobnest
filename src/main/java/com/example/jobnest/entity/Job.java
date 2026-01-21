package com.example.jobnest.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Job {

    private static final String PAYMENT_STATUS_PENDING = "PENDING";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int jobId;

    @NotBlank(message = "Job title cannot be empty")
    @Size(max = 200, message = "Job title cannot exceed 200 characters")
    @Column(nullable = false)
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    @Column(length = 2000)
    private String description;

    private String location;

    private String city;

    private String state;

    private String country;

    private String employmentType;

    private String salary;

    @Column(name = "is_active")
    private Boolean active;

    @Column(length = 50)
    private String paymentStatus = PAYMENT_STATUS_PENDING;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private PricingPlan plan;

    @Column(name = "is_featured")
    private Boolean featured = false;

    @Column(name = "is_premium")
    private Boolean premium = false;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date postedDate;

    @ManyToOne
    @JoinColumn(name = "recruiter_id")
    private RecruiterProfile recruiter;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<JobSeekerApply> jobSeekerApplyList;

    @PrePersist
    protected void onCreate() {
        postedDate = new Date();
        if (active == null) {
            active = false; // Job starts inactive until payment is confirmed
        }
        if (featured == null) {
            featured = false;
        }
        if (premium == null) {
            premium = false;
        }
        if (paymentStatus == null) {
            paymentStatus = PAYMENT_STATUS_PENDING;
        }
    }

    @PostLoad
    protected void onLoad() {
        // Ensure null values are set to defaults when loading from database
        if (active == null) {
            active = false;
        }
        if (featured == null) {
            featured = false;
        }
        if (premium == null) {
            premium = false;
        }
        if (paymentStatus == null) {
            paymentStatus = PAYMENT_STATUS_PENDING;
        }
    }

    // Helper methods for boolean fields (Boolean wrapper to boolean primitive)
    public boolean isActive() {
        return active != null && active;
    }

    public boolean isFeatured() {
        return featured != null && featured;
    }

    public boolean isPremium() {
        return premium != null && premium;
    }

    // Helper methods for payment and plan features
    public boolean isPaid() {
        return "PAID".equalsIgnoreCase(paymentStatus);
    }

    public boolean isPending() {
        return PAYMENT_STATUS_PENDING.equalsIgnoreCase(paymentStatus);
    }
}
