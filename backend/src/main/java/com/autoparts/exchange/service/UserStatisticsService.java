package com.autoparts.exchange.service;

import com.autoparts.exchange.entity.User;
import com.autoparts.exchange.repository.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserStatisticsService {
    
    private final UserRepository userRepository;
    
    @Async
    @Transactional
    @CacheEvict(value = {"users", "userStats"}, key = "#buyer.id")
    public CompletableFuture<Void> updateBuyerStatistics(User buyer) {
        buyer.setTotalPurchases(buyer.getTotalPurchases() + 1);
        userRepository.save(buyer);
        
        log.info("Updated buyer statistics for user: {}", buyer.getEmail());
        return CompletableFuture.completedFuture(null);
    }
    
    @Async
    @Transactional
    @CacheEvict(value = {"users", "userStats"}, key = "#seller.id")
    public CompletableFuture<Void> updateSellerStatistics(User seller) {
        seller.setTotalSales(seller.getTotalSales() + 1);
        
        // Improve trust score based on successful sales
        double currentScore = seller.getTrustScore();
        double improvement = calculateTrustScoreImprovement(seller.getTotalSales());
        double newTrustScore = Math.min(5.0, currentScore + improvement);
        seller.setTrustScore(newTrustScore);
        
        userRepository.save(seller);
        
        log.info("Updated seller statistics for user: {} - New trust score: {}", 
            seller.getEmail(), newTrustScore);
        return CompletableFuture.completedFuture(null);
    }
    
    private double calculateTrustScoreImprovement(int totalSales) {
        // Diminishing returns for trust score improvement
        if (totalSales <= 5) return 0.2;
        if (totalSales <= 20) return 0.1;
        if (totalSales <= 50) return 0.05;
        return 0.02;
    }
}
