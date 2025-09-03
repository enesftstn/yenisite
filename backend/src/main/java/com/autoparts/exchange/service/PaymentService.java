package com.autoparts.exchange.service;

import com.autoparts.exchange.entity.Order;
import com.autoparts.exchange.entity.Payment;
import com.autoparts.exchange.repository.jpa.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    
    @Transactional
    public Payment processPayment(Order order, Payment.PaymentMethod paymentMethod) {
        // Create payment record
        Payment payment = Payment.builder()
                .order(order)
                .paymentIntentId(generatePaymentIntentId())
                .transactionId(generateTransactionId())
                .amount(order.getTotalAmount())
                .currency("USD")
                .paymentMethod(paymentMethod)
                .status(Payment.PaymentStatus.PROCESSING)
                .paymentGateway("MOCK_GATEWAY")
                .build();
        
        Payment savedPayment = paymentRepository.save(payment);
        
        try {
            // Mock payment processing
            boolean paymentSuccess = mockPaymentProcessing(savedPayment);
            
            if (paymentSuccess) {
                savedPayment.setStatus(Payment.PaymentStatus.COMPLETED);
                savedPayment.setProcessedAt(LocalDateTime.now());
                savedPayment.setGatewayResponse("Payment processed successfully");
                
                log.info("Payment processed successfully for order: {}", order.getOrderNumber());
            } else {
                savedPayment.setStatus(Payment.PaymentStatus.FAILED);
                savedPayment.setFailureReason("Mock payment failure");
                savedPayment.setGatewayResponse("Payment processing failed");
                
                log.error("Payment processing failed for order: {}", order.getOrderNumber());
                throw new RuntimeException("Payment processing failed");
            }
            
        } catch (Exception e) {
            savedPayment.setStatus(Payment.PaymentStatus.FAILED);
            savedPayment.setFailureReason(e.getMessage());
            log.error("Payment processing error for order: {}", order.getOrderNumber(), e);
            throw e;
        }
        
        return paymentRepository.save(savedPayment);
    }
    
    private boolean mockPaymentProcessing(Payment payment) {
        // Mock payment processing - 95% success rate
        return Math.random() > 0.05;
    }
    
    private String generatePaymentIntentId() {
        return "pi_" + UUID.randomUUID().toString().replace("-", "");
    }
    
    private String generateTransactionId() {
        return "txn_" + UUID.randomUUID().toString().replace("-", "");
    }
}
