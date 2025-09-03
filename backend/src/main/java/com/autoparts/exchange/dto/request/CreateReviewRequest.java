package com.autoparts.exchange.dto.request;

import com.autoparts.exchange.entity.Review;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {
    
    @NotNull(message = "Order ID is required")
    private Long orderId;
    
    @NotNull(message = "Reviewee ID is required")
    private Long revieweeId;
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;
    
    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    private String comment;
    
    @NotNull(message = "Review type is required")
    private Review.ReviewType type;
}
