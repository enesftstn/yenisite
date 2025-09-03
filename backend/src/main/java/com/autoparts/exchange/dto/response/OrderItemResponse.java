package com.autoparts.exchange.dto.response;

import com.autoparts.exchange.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    
    private Long id;
    private AutoPartResponse autoPart;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    
    // Snapshot data
    private String partName;
    private String partBrand;
    private String partNumber;
    private String partCondition;
    
    public static OrderItemResponse fromEntity(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .autoPart(orderItem.getAutoPart() != null ? AutoPartResponse.fromEntity(orderItem.getAutoPart()) : null)
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .totalPrice(orderItem.getTotalPrice())
                .partName(orderItem.getPartName())
                .partBrand(orderItem.getPartBrand())
                .partNumber(orderItem.getPartNumber())
                .partCondition(orderItem.getPartCondition())
                .build();
    }
}
