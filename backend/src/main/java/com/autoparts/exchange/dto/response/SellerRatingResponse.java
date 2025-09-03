package com.autoparts.exchange.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerRatingResponse {
    
    private Long sellerId;
    private String sellerName;
    private Double averageRating;
    private Long totalReviews;
    private Double trustScore;
    private Integer totalSales;
}
