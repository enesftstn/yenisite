package com.autoparts.exchange.controller;

import com.autoparts.exchange.dto.request.AddFavoriteRequest;
import com.autoparts.exchange.dto.response.ApiResponse;
import com.autoparts.exchange.dto.response.FavoriteResponse;
import com.autoparts.exchange.service.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FavoriteController {
    
    private final FavoriteService favoriteService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<FavoriteResponse>> addToFavorites(
            @Valid @RequestBody AddFavoriteRequest request,
            Authentication authentication) {
        
        FavoriteResponse response = favoriteService.addToFavorites(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Added to favorites successfully", response));
    }
    
    @DeleteMapping("/{autoPartId}")
    public ResponseEntity<ApiResponse<String>> removeFromFavorites(
            @PathVariable Long autoPartId,
            Authentication authentication) {
        
        favoriteService.removeFromFavorites(autoPartId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Removed from favorites successfully", "OK"));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<FavoriteResponse>>> getFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        
        Page<FavoriteResponse> response = favoriteService.getFavorites(authentication.getName(), page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/check/{autoPartId}")
    public ResponseEntity<ApiResponse<Boolean>> isFavorite(
            @PathVariable Long autoPartId,
            Authentication authentication) {
        
        boolean isFavorite = favoriteService.isFavorite(autoPartId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(isFavorite));
    }
    
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getFavoriteCount(Authentication authentication) {
        Long count = favoriteService.getFavoriteCount(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
