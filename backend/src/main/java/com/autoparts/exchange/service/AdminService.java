package com.autoparts.exchange.service;

import com.autoparts.exchange.dto.response.UserResponse;
import com.autoparts.exchange.entity.User;
import com.autoparts.exchange.exception.ResourceNotFoundException;
import com.autoparts.exchange.repository.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserRepository userRepository;

    public Page<UserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable)
                .map(this::convertToUserResponse);
    }

    public UserResponse updateUserRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        User.Role role = User.Role.valueOf(roleName.toUpperCase());
        user.setRole(role);
        user = userRepository.save(user);
        
        return convertToUserResponse(user);
    }

    public void banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);
    }

    public void unbanUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setIsActive(true);
        userRepository.save(user);
    }

    public Object getSystemStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("activeUsers", userRepository.countByIsActiveTrue());
        stats.put("adminUsers", userRepository.countByRole(User.Role.ADMIN));
        stats.put("moderatorUsers", userRepository.countByRole(User.Role.MODERATOR));
        return stats;
    }

    private UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .isVerified(user.getIsVerified())
                .trustScore(user.getTrustScore())
                .totalSales(user.getTotalSales())
                .totalPurchases(user.getTotalPurchases())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
