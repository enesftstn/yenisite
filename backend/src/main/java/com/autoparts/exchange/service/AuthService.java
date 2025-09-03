package com.autoparts.exchange.service;

import com.autoparts.exchange.dto.request.LoginRequest;
import com.autoparts.exchange.dto.request.RegisterRequest;
import com.autoparts.exchange.dto.response.AuthResponse;
import com.autoparts.exchange.dto.response.UserResponse;
import com.autoparts.exchange.entity.User;
import com.autoparts.exchange.exception.ResourceNotFoundException;
import com.autoparts.exchange.repository.jpa.UserRepository;
import com.autoparts.exchange.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        // Create new user
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .country(request.getCountry())
                .role(User.Role.USER)
                .isVerified(false) // Email verification required
                .verificationToken(UUID.randomUUID().toString())
                .isActive(true)
                .build();
        
        User savedUser = userRepository.save(user);
        
        // Generate JWT token
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", savedUser.getId());
        extraClaims.put("role", savedUser.getRole().name());
        
        String token = jwtUtil.generateToken(savedUser, extraClaims);
        
        log.info("User registered successfully: {}", savedUser.getEmail());
        
        return AuthResponse.builder()
                .token(token)
                .user(UserResponse.fromUser(savedUser))
                .build();
    }
    
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        User user = (User) authentication.getPrincipal();
        
        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        
        // Generate JWT token
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getId());
        extraClaims.put("role", user.getRole().name());
        
        String token = jwtUtil.generateToken(user, extraClaims);
        
        log.info("User logged in successfully: {}", user.getEmail());
        
        return AuthResponse.builder()
                .token(token)
                .user(UserResponse.fromUser(user))
                .build();
    }
    
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        
        return UserResponse.fromUser(user);
    }
    
    @Transactional
    public UserResponse verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid verification token"));
        
        user.setIsVerified(true);
        user.setVerificationToken(null);
        User savedUser = userRepository.save(user);
        
        log.info("Email verified for user: {}", savedUser.getEmail());
        
        return UserResponse.fromUser(savedUser);
    }
}
