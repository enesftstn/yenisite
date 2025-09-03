package com.autoparts.exchange.controller;

import com.autoparts.exchange.dto.request.CreateReviewRequest;
import com.autoparts.exchange.dto.response.ApiResponse;
import com.autoparts.exchange.dto.response.ReviewResponse;
import com.autoparts.exchange.dto.response.SellerRatingResponse;
import com.autoparts.exchange.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReviewController {
    
    private final ReviewService reviewService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody CreateReviewRequest request,
            Authentication authentication) {
        
        ReviewResponse response = reviewService.createReview(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review created successfully", response));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getReviewsForUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<ReviewResponse> response = reviewService.getReviewsForUser(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/my-reviews")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getMyReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        
        Page<ReviewResponse> response = reviewService.getReviewsByUser(authentication.getName(), page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/seller/{sellerId}/rating")
    public ResponseEntity<ApiResponse<SellerRatingResponse>> getSellerRating(@PathVariable Long sellerId) {
        SellerRatingResponse response = reviewService.getSellerRating(sellerId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
