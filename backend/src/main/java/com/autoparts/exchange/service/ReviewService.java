package com.autoparts.exchange.service;

import com.autoparts.exchange.dto.request.CreateReviewRequest;
import com.autoparts.exchange.dto.response.ReviewResponse;
import com.autoparts.exchange.dto.response.SellerRatingResponse;
import com.autoparts.exchange.entity.Order;
import com.autoparts.exchange.entity.Review;
import com.autoparts.exchange.entity.User;
import com.autoparts.exchange.exception.ResourceNotFoundException;
import com.autoparts.exchange.repository.jpa.OrderRepository;
import com.autoparts.exchange.repository.jpa.ReviewRepository;
import com.autoparts.exchange.repository.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    
    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request, String reviewerEmail) {
        User reviewer = userRepository.findByEmail(reviewerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", reviewerEmail));
        
        User reviewee = userRepository.findById(request.getRevieweeId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getRevieweeId()));
        
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", request.getOrderId()));
        
        // Validate that reviewer was part of this order
        if (!order.getBuyer().getId().equals(reviewer.getId()) && 
            !order.getSeller().getId().equals(reviewer.getId())) {
            throw new RuntimeException("You can only review orders you were part of");
        }
        
        // Check if review already exists for this order
        if (reviewRepository.existsByReviewerAndOrderId(reviewer, request.getOrderId())) {
            throw new RuntimeException("You have already reviewed this order");
        }
        
        // Validate review type matches the relationship
        if (request.getType() == Review.ReviewType.SELLER && !order.getSeller().getId().equals(reviewee.getId())) {
            throw new RuntimeException("Invalid reviewee for seller review");
        }
        if (request.getType() == Review.ReviewType.BUYER && !order.getBuyer().getId().equals(reviewee.getId())) {
            throw new RuntimeException("Invalid reviewee for buyer review");
        }
        
        Review review = Review.builder()
                .reviewer(reviewer)
                .reviewee(reviewee)
                .order(order)
                .rating(request.getRating())
                .comment(request.getComment())
                .type(request.getType())
                .isVerified(true) // Verified because it's based on actual transaction
                .build();
        
        Review savedReview = reviewRepository.save(review);
        
        // Update reviewee's trust score
        updateUserTrustScore(reviewee);
        
        log.info("Review created by {} for {} with rating {}", reviewerEmail, reviewee.getEmail(), request.getRating());
        
        return ReviewResponse.fromEntity(savedReview);
    }
    
    public Page<ReviewResponse> getReviewsForUser(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewRepository.findByRevieweeOrderByCreatedAtDesc(user, pageable);
        
        return reviews.map(ReviewResponse::fromEntity);
    }
    
    public Page<ReviewResponse> getReviewsByUser(String userEmail, int page, int size) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewRepository.findByReviewerOrderByCreatedAtDesc(user, pageable);
        
        return reviews.map(ReviewResponse::fromEntity);
    }
    
    public SellerRatingResponse getSellerRating(Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", sellerId));
        
        Double averageRating = reviewRepository.getAverageRatingForSeller(seller);
        Long totalReviews = reviewRepository.getReviewCountForSeller(seller);
        
        return SellerRatingResponse.builder()
                .sellerId(seller.getId())
                .sellerName(seller.getFirstName() + " " + seller.getLastName())
                .averageRating(averageRating != null ? averageRating : 0.0)
                .totalReviews(totalReviews != null ? totalReviews : 0L)
                .trustScore(seller.getTrustScore())
                .totalSales(seller.getTotalSales())
                .build();
    }
    
    private void updateUserTrustScore(User user) {
        Double averageRating = reviewRepository.getAverageRatingForSeller(user);
        Long reviewCount = reviewRepository.getReviewCountForSeller(user);
        
        if (averageRating != null && reviewCount != null && reviewCount > 0) {
            // Calculate trust score based on average rating and number of reviews
            double trustScore = averageRating * (1 + Math.log10(reviewCount + 1)) / 2;
            trustScore = Math.min(5.0, Math.max(0.0, trustScore)); // Clamp between 0-5
            
            user.setTrustScore(trustScore);
            userRepository.save(user);
        }
    }
}
