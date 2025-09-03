package com.autoparts.exchange.repository.elasticsearch;

import com.autoparts.exchange.document.AutoPartDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface AutoPartSearchRepository extends ElasticsearchRepository<AutoPartDocument, String> {
    
    Page<AutoPartDocument> findByStatusAndIsAvailableOrderByCreatedAtDesc(String status, Boolean isAvailable, Pageable pageable);
    
    Page<AutoPartDocument> findByIsFeaturedTrueAndStatusAndIsAvailableOrderByCreatedAtDesc(String status, Boolean isAvailable, Pageable pageable);
    
    @Query("{\"bool\": {\"must\": [{\"match\": {\"status\": \"ACTIVE\"}}, {\"match\": {\"isAvailable\": true}}], \"should\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name^3\", \"description^2\", \"brand^2\", \"vehicleMake^2\", \"vehicleModel^2\", \"partNumber^3\", \"oemNumber^3\"], \"type\": \"best_fields\", \"fuzziness\": \"AUTO\"}}], \"minimum_should_match\": 1}}")
    Page<AutoPartDocument> searchByKeyword(String keyword, Pageable pageable);
    
    @Query("{\"bool\": {\"must\": [{\"match\": {\"status\": \"ACTIVE\"}}, {\"match\": {\"isAvailable\": true}}], \"filter\": [{\"range\": {\"price\": {\"gte\": ?0, \"lte\": ?1}}}]}}")
    Page<AutoPartDocument> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    Page<AutoPartDocument> findByCategoryAndStatusAndIsAvailableOrderByCreatedAtDesc(String category, String status, Boolean isAvailable, Pageable pageable);
    
    Page<AutoPartDocument> findByBrandAndStatusAndIsAvailableOrderByCreatedAtDesc(String brand, String status, Boolean isAvailable, Pageable pageable);
    
    Page<AutoPartDocument> findByVehicleMakeAndStatusAndIsAvailableOrderByCreatedAtDesc(String vehicleMake, String status, Boolean isAvailable, Pageable pageable);
    
    Page<AutoPartDocument> findByVehicleMakeAndVehicleModelAndStatusAndIsAvailableOrderByCreatedAtDesc(String vehicleMake, String vehicleModel, String status, Boolean isAvailable, Pageable pageable);
    
    Page<AutoPartDocument> findByStateAndStatusAndIsAvailableOrderByCreatedAtDesc(String state, String status, Boolean isAvailable, Pageable pageable);
    
    Page<AutoPartDocument> findBySellerIdOrderByCreatedAtDesc(Long sellerId, Pageable pageable);
}
