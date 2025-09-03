package com.autoparts.exchange.repository.jpa;

import com.autoparts.exchange.entity.AutoPart;
import com.autoparts.exchange.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AutoPartRepository extends JpaRepository<AutoPart, Long> {
    
    Page<AutoPart> findBySellerAndStatusOrderByCreatedAtDesc(User seller, AutoPart.Status status, Pageable pageable);
    
    Page<AutoPart> findByStatusAndIsAvailableOrderByCreatedAtDesc(AutoPart.Status status, Boolean isAvailable, Pageable pageable);
    
    Page<AutoPart> findByIsFeaturedTrueAndStatusAndIsAvailableOrderByCreatedAtDesc(AutoPart.Status status, Boolean isAvailable, Pageable pageable);
    
    @Query("SELECT ap FROM AutoPart ap WHERE ap.status = 'ACTIVE' AND ap.isAvailable = true " +
           "AND (:category IS NULL OR ap.category = :category) " +
           "AND (:brand IS NULL OR LOWER(ap.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) " +
           "AND (:vehicleMake IS NULL OR LOWER(ap.vehicleMake) LIKE LOWER(CONCAT('%', :vehicleMake, '%'))) " +
           "AND (:minPrice IS NULL OR ap.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR ap.price <= :maxPrice) " +
           "ORDER BY ap.createdAt DESC")
    Page<AutoPart> findWithFilters(@Param("category") AutoPart.Category category,
                                  @Param("brand") String brand,
                                  @Param("vehicleMake") String vehicleMake,
                                  @Param("minPrice") BigDecimal minPrice,
                                  @Param("maxPrice") BigDecimal maxPrice,
                                  Pageable pageable);
    
    @Query("SELECT DISTINCT ap.brand FROM AutoPart ap WHERE ap.status = 'ACTIVE' ORDER BY ap.brand")
    List<String> findAllBrands();
    
    @Query("SELECT DISTINCT ap.vehicleMake FROM AutoPart ap WHERE ap.status = 'ACTIVE' ORDER BY ap.vehicleMake")
    List<String> findAllVehicleMakes();
    
    @Query("SELECT COUNT(ap) FROM AutoPart ap WHERE ap.seller = :seller AND ap.status = 'ACTIVE'")
    Long countActiveBySeller(@Param("seller") User seller);
    
    @Query("SELECT COUNT(ap) FROM AutoPart ap WHERE ap.seller = :seller AND ap.status = 'SOLD'")
    Long countSoldBySeller(@Param("seller") User seller);
}
