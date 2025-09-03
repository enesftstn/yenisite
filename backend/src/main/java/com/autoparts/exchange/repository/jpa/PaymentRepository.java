package com.autoparts.exchange.repository.jpa;

import com.autoparts.exchange.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByPaymentIntentId(String paymentIntentId);
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    Optional<Payment> findByOrderId(Long orderId);
}
