package com.autoparts.exchange.dto.request;

import com.autoparts.exchange.entity.AutoPart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoPartSearchRequest {
    
    private String keyword;
    private String brand;
    private String vehicleMake;
    private String vehicleModel;
    private Integer vehicleYear;
    private AutoPart.Category category;
    private AutoPart.Condition condition;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String location;
    private String state;
    private String sortBy = "createdAt"; // createdAt, price, viewCount
    private String sortDirection = "desc"; // asc, desc
    private Integer page = 0;
    private Integer size = 20;
}
