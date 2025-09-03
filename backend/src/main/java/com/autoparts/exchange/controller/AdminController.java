package com.autoparts.exchange.controller;

import com.autoparts.exchange.dto.response.ApiResponse;
import com.autoparts.exchange.dto.response.UserResponse;
import com.autoparts.exchange.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<UserResponse> users = adminService.getAllUsers(page, size);
        return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully"));
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(
            @PathVariable Long userId,
            @RequestParam String role) {
        UserResponse user = adminService.updateUserRole(userId, role);
        return ResponseEntity.ok(ApiResponse.success(user, "User role updated successfully"));
    }

    @PutMapping("/users/{userId}/ban")
    public ResponseEntity<ApiResponse<Void>> banUser(@PathVariable Long userId) {
        adminService.banUser(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "User banned successfully"));
    }

    @PutMapping("/users/{userId}/unban")
    public ResponseEntity<ApiResponse<Void>> unbanUser(@PathVariable Long userId) {
        adminService.unbanUser(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "User unbanned successfully"));
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Object>> getSystemStatistics() {
        Object stats = adminService.getSystemStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats, "Statistics retrieved successfully"));
    }
}
