package com.autoparts.exchange.repository.jpa;

import com.autoparts.exchange.entity.Review;
import com.autoparts.exchange.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    Page<Review> findByRevieweeOrderByCreatedAtDesc(User reviewee, Pageable pageable);
    
    Page<Review> findByReviewerOrderByCreatedAtDesc(User reviewer, Pageable pageable);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewee = :user AND r.type = 'SELLER'")
    Double getAverageRatingForSeller(@Param("user") User user);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.reviewee = :user AND r.type = 'SELLER'")
    Long getReviewCountForSeller(@Param("user") User user);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewee = :user AND r.type = 'BUYER'")
    Double getAverageRatingForBuyer(@Param("user") User user);
    
    boolean existsByReviewerAndOrderId(User reviewer, Long orderId);
}
