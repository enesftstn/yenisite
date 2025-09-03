package com.autoparts.exchange.dto.request;

import com.autoparts.exchange.entity.AutoPart;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoPartRequest {
    
    @NotBlank(message = "Part name is required")
    private String name;
    
    private String description;
    
    @NotBlank(message = "Brand is required")
    private String brand;
    
    private String vehicleMake;
    private String vehicleModel;
    
    @Min(value = 1900, message = "Vehicle year must be valid")
    @Max(value = 2030, message = "Vehicle year must be valid")
    private Integer vehicleYear;
    
    private String partNumber;
    private String oemNumber;
    
    @NotNull(message = "Category is required")
    private AutoPart.Category category;
    
    @NotNull(message = "Condition is required")
    private AutoPart.Condition condition;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity = 1;
    
    private List<String> imageUrls;
    
    private String location;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    
    private String engineType;
    private String transmissionType;
    private String color;
    private String material;
    private String weight;
    private String dimensions;
}
