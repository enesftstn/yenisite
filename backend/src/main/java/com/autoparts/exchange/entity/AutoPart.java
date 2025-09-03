package com.autoparts.exchange.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "auto_parts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoPart {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String brand;
    
    @Column(name = "vehicle_make")
    private String vehicleMake;
    
    @Column(name = "vehicle_model")
    private String vehicleModel;
    
    @Column(name = "vehicle_year")
    private Integer vehicleYear;
    
    @Column(name = "part_number")
    private String partNumber;
    
    @Column(name = "oem_number")
    private String oemNumber;
    
    @Enumerated(EnumType.STRING)
    private Category category;
    
    @Enumerated(EnumType.STRING)
    private Condition condition;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;
    
    @Column(nullable = false)
    private Integer quantity = 1;
    
    @Column(name = "is_available")
    private Boolean isAvailable = true;
    
    @ElementCollection
    @CollectionTable(name = "auto_part_images", joinColumns = @JoinColumn(name = "auto_part_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;
    
    // Seller information
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;
    
    // Location information
    private String location;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    
    // Additional specifications
    @Column(name = "engine_type")
    private String engineType;
    
    @Column(name = "transmission_type")
    private String transmissionType;
    
    private String color;
    private String material;
    private String weight;
    private String dimensions;
    
    // Marketplace metadata
    @Column(name = "view_count")
    private Long viewCount = 0L;
    
    @Column(name = "favorite_count")
    private Long favoriteCount = 0L;
    
    @Column(name = "is_featured")
    private Boolean isFeatured = false;
    
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // Auto parts listings expire after 90 days
        expiresAt = LocalDateTime.now().plusDays(90);
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum Category {
        ENGINE, TRANSMISSION, BRAKES, SUSPENSION, ELECTRICAL, 
        BODY_PARTS, INTERIOR, EXHAUST, COOLING, FUEL_SYSTEM,
        STEERING, WHEELS_TIRES, LIGHTING, ACCESSORIES, OTHER
    }
    
    public enum Condition {
        NEW, LIKE_NEW, GOOD, FAIR, POOR, SALVAGE, REBUILT, REFURBISHED
    }
    
    public enum Status {
        ACTIVE, SOLD, EXPIRED, SUSPENDED, DRAFT
    }
}
