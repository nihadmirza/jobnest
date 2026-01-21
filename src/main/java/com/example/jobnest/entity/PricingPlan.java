package com.example.jobnest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "pricing_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PricingPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int planId;

    @Column(nullable = false, unique = true, length = 50)
    private String planName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int durationDays;

    @Column(length = 2000)
    private String featuresJson;

    @Column(nullable = false)
    private boolean isActive = true;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @PrePersist
    protected void onCreate() {
        createdDate = new Date();
    }

    // Helper methods
    public String getFormattedPrice() {
        return String.format("$%.2f", price);
    }

    public boolean isBasic() {
        return "Basic".equalsIgnoreCase(planName);
    }

    public boolean isFeatured() {
        return "Featured".equalsIgnoreCase(planName);
    }

    public boolean isPremium() {
        return "Premium".equalsIgnoreCase(planName);
    }
}
