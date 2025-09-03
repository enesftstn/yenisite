package com.autoparts.exchange.controller;

import com.autoparts.exchange.dto.request.LoginRequest;
import com.autoparts.exchange.dto.request.RegisterRequest;
import com.autoparts.exchange.dto.response.ApiResponse;
import com.autoparts.exchange.dto.response.AuthResponse;
import com.autoparts.exchange.dto.response.UserResponse;
import com.autoparts.exchange.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication authentication) {
        UserResponse user = authService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<UserResponse>> verifyEmail(@RequestParam String token) {
        UserResponse user = authService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully", user));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        // JWT is stateless, so logout is handled on the client side
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", "OK"));
    }
}
