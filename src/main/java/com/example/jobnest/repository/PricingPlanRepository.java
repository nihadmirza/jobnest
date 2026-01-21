package com.example.jobnest.repository;

import com.example.jobnest.entity.PricingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PricingPlanRepository extends JpaRepository<PricingPlan, Integer> {

    List<PricingPlan> findByIsActiveTrue();

    Optional<PricingPlan> findByPlanName(String planName);
}
