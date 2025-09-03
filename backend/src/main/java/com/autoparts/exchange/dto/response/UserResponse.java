package com.autoparts.exchange.dto.response;

import com.autoparts.exchange.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String profileImageUrl;
    private User.Role role;
    private Boolean isVerified;
    private Boolean isActive;
    private Double trustScore;
    private Integer totalSales;
    private Integer totalPurchases;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    
    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .city(user.getCity())
                .state(user.getState())
                .zipCode(user.getZipCode())
                .country(user.getCountry())
                .profileImageUrl(user.getProfileImageUrl())
                .role(user.getRole())
                .isVerified(user.getIsVerified())
                .isActive(user.getIsActive())
                .trustScore(user.getTrustScore())
                .totalSales(user.getTotalSales())
                .totalPurchases(user.getTotalPurchases())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }
}
