package com.autoparts.exchange.repository.jpa;

import com.autoparts.exchange.entity.Order;
import com.autoparts.exchange.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    Page<Order> findByBuyerOrderByCreatedAtDesc(User buyer, Pageable pageable);
    
    Page<Order> findBySellerOrderByCreatedAtDesc(User seller, Pageable pageable);
    
    Page<Order> findByBuyerAndStatusOrderByCreatedAtDesc(User buyer, Order.OrderStatus status, Pageable pageable);
    
    Page<Order> findBySellerAndStatusOrderByCreatedAtDesc(User seller, Order.OrderStatus status, Pageable pageable);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.buyer = :user")
    Long countByBuyer(@Param("user") User user);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.seller = :user")
    Long countBySeller(@Param("user") User user);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.seller = :user AND o.status = 'COMPLETED'")
    Long countCompletedSalesBySeller(@Param("user") User user);
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.seller = :user AND o.status = 'COMPLETED'")
    java.math.BigDecimal getTotalSalesAmountBySeller(@Param("user") User user);
    
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    Page<Order> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                               @Param("endDate") LocalDateTime endDate, 
                               Pageable pageable);
}
