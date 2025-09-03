package com.autoparts.exchange.dto.response;

import com.autoparts.exchange.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    
    private Long id;
    private UserResponse reviewer;
    private UserResponse reviewee;
    private Integer rating;
    private String comment;
    private Review.ReviewType type;
    private Boolean isVerified;
    private Integer helpfulCount;
    private LocalDateTime createdAt;
    
    public static ReviewResponse fromEntity(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .reviewer(UserResponse.fromUser(review.getReviewer()))
                .reviewee(UserResponse.fromUser(review.getReviewee()))
                .rating(review.getRating())
                .comment(review.getComment())
                .type(review.getType())
                .isVerified(review.getIsVerified())
                .helpfulCount(review.getHelpfulCount())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
