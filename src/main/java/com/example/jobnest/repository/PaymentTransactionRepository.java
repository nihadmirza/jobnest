package com.example.jobnest.repository;

import com.example.jobnest.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Integer> {

    Optional<PaymentTransaction> findByStripeSessionId(String sessionId);

    List<PaymentTransaction> findByRecruiterUserAccountId(int recruiterId);

    List<PaymentTransaction> findByPaymentStatus(String status);

    Optional<PaymentTransaction> findByJobJobId(int jobId);
}
