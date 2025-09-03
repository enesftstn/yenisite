package com.autoparts.exchange.dto.response;

import com.autoparts.exchange.entity.AutoPart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoPartResponse {
    
    private Long id;
    private String name;
    private String description;
    private String brand;
    private String vehicleMake;
    private String vehicleModel;
    private Integer vehicleYear;
    private String partNumber;
    private String oemNumber;
    private AutoPart.Category category;
    private AutoPart.Condition condition;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer quantity;
    private Boolean isAvailable;
    private List<String> imageUrls;
    
    // Seller information
    private UserResponse seller;
    
    // Location
    private String location;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    
    // Additional specs
    private String engineType;
    private String transmissionType;
    private String color;
    private String material;
    private String weight;
    private String dimensions;
    
    // Metadata
    private Long viewCount;
    private Long favoriteCount;
    private Boolean isFeatured;
    private AutoPart.Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiresAt;
    
    public static AutoPartResponse fromEntity(AutoPart autoPart) {
        return AutoPartResponse.builder()
                .id(autoPart.getId())
                .name(autoPart.getName())
                .description(autoPart.getDescription())
                .brand(autoPart.getBrand())
                .vehicleMake(autoPart.getVehicleMake())
                .vehicleModel(autoPart.getVehicleModel())
                .vehicleYear(autoPart.getVehicleYear())
                .partNumber(autoPart.getPartNumber())
                .oemNumber(autoPart.getOemNumber())
                .category(autoPart.getCategory())
                .condition(autoPart.getCondition())
                .price(autoPart.getPrice())
                .originalPrice(autoPart.getOriginalPrice())
                .quantity(autoPart.getQuantity())
                .isAvailable(autoPart.getIsAvailable())
                .imageUrls(autoPart.getImageUrls())
                .seller(UserResponse.fromUser(autoPart.getSeller()))
                .location(autoPart.getLocation())
                .city(autoPart.getCity())
                .state(autoPart.getState())
                .zipCode(autoPart.getZipCode())
                .country(autoPart.getCountry())
                .engineType(autoPart.getEngineType())
                .transmissionType(autoPart.getTransmissionType())
                .color(autoPart.getColor())
                .material(autoPart.getMaterial())
                .weight(autoPart.getWeight())
                .dimensions(autoPart.getDimensions())
                .viewCount(autoPart.getViewCount())
                .favoriteCount(autoPart.getFavoriteCount())
                .isFeatured(autoPart.getIsFeatured())
                .status(autoPart.getStatus())
                .createdAt(autoPart.getCreatedAt())
                .updatedAt(autoPart.getUpdatedAt())
                .expiresAt(autoPart.getExpiresAt())
                .build();
    }
}
