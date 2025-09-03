package com.autoparts.exchange.service;

import com.autoparts.exchange.entity.Order;
import com.autoparts.exchange.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCalculationService {
    
    private static final BigDecimal FREE_SHIPPING_THRESHOLD = BigDecimal.valueOf(100);
    private static final BigDecimal FLAT_SHIPPING_RATE = BigDecimal.valueOf(15.99);
    private static final BigDecimal TAX_RATE = BigDecimal.valueOf(0.085); // 8.5%
    
    public BigDecimal calculateSubtotal(List<OrderItem> orderItems) {
        return orderItems.stream()
            .map(OrderItem::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Cacheable(value = "shipping", key = "#subtotal + '_' + #zipCode")
    public BigDecimal calculateShippingCost(BigDecimal subtotal, String zipCode) {
        // Free shipping over threshold
        if (subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0) {
            return BigDecimal.ZERO;
        }
        
        // TODO: Implement zone-based shipping calculation using zipCode
        return FLAT_SHIPPING_RATE;
    }
    
    @Cacheable(value = "tax", key = "#subtotal + '_' + #state")
    public BigDecimal calculateTax(BigDecimal subtotal, String state) {
        // TODO: Implement state-specific tax rates
        return subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal calculateTotal(BigDecimal subtotal, BigDecimal shipping, BigDecimal tax) {
        return subtotal.add(shipping).add(tax);
    }
    
    public Order.OrderTotals calculateOrderTotals(List<OrderItem> orderItems, String zipCode, String state) {
        BigDecimal subtotal = calculateSubtotal(orderItems);
        BigDecimal shipping = calculateShippingCost(subtotal, zipCode);
        BigDecimal tax = calculateTax(subtotal, state);
        BigDecimal total = calculateTotal(subtotal, shipping, tax);
        
        return Order.OrderTotals.builder()
            .subtotal(subtotal)
            .shippingCost(shipping)
            .taxAmount(tax)
            .totalAmount(total)
            .build();
    }
}
