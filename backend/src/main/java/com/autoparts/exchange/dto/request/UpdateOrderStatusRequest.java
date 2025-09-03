package com.autoparts.exchange.dto.request;

import com.autoparts.exchange.entity.Order;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {
    
    @NotNull(message = "Order status is required")
    private Order.OrderStatus status;
    
    private String trackingNumber;
    private String shippingCarrier;
    private String sellerNotes;
}
