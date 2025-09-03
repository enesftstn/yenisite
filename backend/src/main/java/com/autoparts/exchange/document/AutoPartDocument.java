package com.autoparts.exchange.document;

import com.autoparts.exchange.entity.AutoPart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(indexName = "auto_parts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoPartDocument {
    
    @Id
    private String id;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;
    
    @Field(type = FieldType.Keyword)
    private String brand;
    
    @Field(type = FieldType.Keyword)
    private String vehicleMake;
    
    @Field(type = FieldType.Keyword)
    private String vehicleModel;
    
    @Field(type = FieldType.Integer)
    private Integer vehicleYear;
    
    @Field(type = FieldType.Keyword)
    private String partNumber;
    
    @Field(type = FieldType.Keyword)
    private String oemNumber;
    
    @Field(type = FieldType.Keyword)
    private String category;
    
    @Field(type = FieldType.Keyword)
    private String condition;
    
    @Field(type = FieldType.Double)
    private BigDecimal price;
    
    @Field(type = FieldType.Integer)
    private Integer quantity;
    
    @Field(type = FieldType.Boolean)
    private Boolean isAvailable;
    
    @Field(type = FieldType.Keyword)
    private List<String> imageUrls;
    
    // Seller information for search
    @Field(type = FieldType.Long)
    private Long sellerId;
    
    @Field(type = FieldType.Text)
    private String sellerName;
    
    @Field(type = FieldType.Double)
    private Double sellerTrustScore;
    
    // Location for geo-search
    @Field(type = FieldType.Text)
    private String location;
    
    @Field(type = FieldType.Keyword)
    private String city;
    
    @Field(type = FieldType.Keyword)
    private String state;
    
    @Field(type = FieldType.Keyword)
    private String zipCode;
    
    @Field(type = FieldType.Keyword)
    private String country;
    
    // Additional searchable fields
    @Field(type = FieldType.Keyword)
    private String engineType;
    
    @Field(type = FieldType.Keyword)
    private String transmissionType;
    
    @Field(type = FieldType.Keyword)
    private String color;
    
    @Field(type = FieldType.Long)
    private Long viewCount;
    
    @Field(type = FieldType.Long)
    private Long favoriteCount;
    
    @Field(type = FieldType.Boolean)
    private Boolean isFeatured;
    
    @Field(type = FieldType.Keyword)
    private String status;
    
    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;
    
    @Field(type = FieldType.Date)
    private LocalDateTime updatedAt;
    
    public static AutoPartDocument fromEntity(AutoPart autoPart) {
        return AutoPartDocument.builder()
                .id(autoPart.getId().toString())
                .name(autoPart.getName())
                .description(autoPart.getDescription())
                .brand(autoPart.getBrand())
                .vehicleMake(autoPart.getVehicleMake())
                .vehicleModel(autoPart.getVehicleModel())
                .vehicleYear(autoPart.getVehicleYear())
                .partNumber(autoPart.getPartNumber())
                .oemNumber(autoPart.getOemNumber())
                .category(autoPart.getCategory() != null ? autoPart.getCategory().name() : null)
                .condition(autoPart.getCondition() != null ? autoPart.getCondition().name() : null)
                .price(autoPart.getPrice())
                .quantity(autoPart.getQuantity())
                .isAvailable(autoPart.getIsAvailable())
                .imageUrls(autoPart.getImageUrls())
                .sellerId(autoPart.getSeller().getId())
                .sellerName(autoPart.getSeller().getFirstName() + " " + autoPart.getSeller().getLastName())
                .sellerTrustScore(autoPart.getSeller().getTrustScore())
                .location(autoPart.getLocation())
                .city(autoPart.getCity())
                .state(autoPart.getState())
                .zipCode(autoPart.getZipCode())
                .country(autoPart.getCountry())
                .engineType(autoPart.getEngineType())
                .transmissionType(autoPart.getTransmissionType())
                .color(autoPart.getColor())
                .viewCount(autoPart.getViewCount())
                .favoriteCount(autoPart.getFavoriteCount())
                .isFeatured(autoPart.getIsFeatured())
                .status(autoPart.getStatus().name())
                .createdAt(autoPart.getCreatedAt())
                .updatedAt(autoPart.getUpdatedAt())
                .build();
    }
}
