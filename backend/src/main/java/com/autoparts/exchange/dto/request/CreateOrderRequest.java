package com.autoparts.exchange.dto.request;

import com.autoparts.exchange.entity.Payment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    
    @NotEmpty(message = "Order items are required")
    private List<OrderItemRequest> items;
    
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;
    
    @NotBlank(message = "Shipping city is required")
    private String shippingCity;
    
    @NotBlank(message = "Shipping state is required")
    private String shippingState;
    
    @NotBlank(message = "Shipping zip code is required")
    private String shippingZipCode;
    
    @NotBlank(message = "Shipping country is required")
    private String shippingCountry;
    
    private String shippingPhone;
    
    @NotNull(message = "Payment method is required")
    private Payment.PaymentMethod paymentMethod;
    
    private String buyerNotes;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        @NotNull(message = "Auto part ID is required")
        private Long autoPartId;
        
        @NotNull(message = "Quantity is required")
        private Integer quantity;
    }
}
