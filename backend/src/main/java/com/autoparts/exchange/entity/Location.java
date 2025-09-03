package com.autoparts.exchange.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "locations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String address;
    
    @Column(nullable = false)
    private String city;
    
    @Column(nullable = false)
    private String state;
    
    @Column(name = "zip_code", nullable = false)
    private String zipCode;
    
    @Column(nullable = false)
    private String country;
    
    @Column(precision = 10, scale = 8)
    private Double latitude;
    
    @Column(precision = 11, scale = 8)
    private Double longitude;
    
    @Column(name = "formatted_address")
    private String formattedAddress;
    
    // Relationships
    @OneToOne(mappedBy = "location")
    private User user;
    
    @OneToOne(mappedBy = "partLocation")
    private AutoPart autoPart;
}
