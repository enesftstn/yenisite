package com.autoparts.exchange.dto.response;

import com.autoparts.exchange.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    
    private Long id;
    private String orderNumber;
    private UserResponse buyer;
    private UserResponse seller;
    private List<OrderItemResponse> orderItems;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private Order.OrderStatus status;
    private Order.PaymentStatus paymentStatus;
    
    // Shipping Information
    private String shippingAddress;
    private String shippingCity;
    private String shippingState;
    private String shippingZipCode;
    private String shippingCountry;
    private String shippingPhone;
    
    // Tracking Information
    private String trackingNumber;
    private String shippingCarrier;
    private LocalDateTime estimatedDelivery;
    
    // Notes
    private String buyerNotes;
    private String sellerNotes;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
    
    public static OrderResponse fromEntity(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .buyer(UserResponse.fromUser(order.getBuyer()))
                .seller(UserResponse.fromUser(order.getSeller()))
                .orderItems(order.getOrderItems() != null ? 
                    order.getOrderItems().stream()
                        .map(OrderItemResponse::fromEntity)
                        .collect(Collectors.toList()) : null)
                .subtotal(order.getSubtotal())
                .shippingCost(order.getShippingCost())
                .taxAmount(order.getTaxAmount())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .shippingAddress(order.getShippingAddress())
                .shippingCity(order.getShippingCity())
                .shippingState(order.getShippingState())
                .shippingZipCode(order.getShippingZipCode())
                .shippingCountry(order.getShippingCountry())
                .shippingPhone(order.getShippingPhone())
                .trackingNumber(order.getTrackingNumber())
                .shippingCarrier(order.getShippingCarrier())
                .estimatedDelivery(order.getEstimatedDelivery())
                .buyerNotes(order.getBuyerNotes())
                .sellerNotes(order.getSellerNotes())
                .createdAt(order.getCreatedAt())
                .confirmedAt(order.getConfirmedAt())
                .shippedAt(order.getShippedAt())
                .deliveredAt(order.getDeliveredAt())
                .cancelledAt(order.getCancelledAt())
                .build();
    }
}
