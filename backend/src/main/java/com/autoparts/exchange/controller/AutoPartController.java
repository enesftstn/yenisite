package com.autoparts.exchange.controller;

import com.autoparts.exchange.dto.request.AutoPartRequest;
import com.autoparts.exchange.dto.request.AutoPartSearchRequest;
import com.autoparts.exchange.dto.response.ApiResponse;
import com.autoparts.exchange.dto.response.AutoPartResponse;
import com.autoparts.exchange.service.AutoPartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AutoPartController {
    
    private final AutoPartService autoPartService;
    
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AutoPartResponse>> createAutoPart(
            @Valid @RequestBody AutoPartRequest request,
            Authentication authentication) {
        
        AutoPartResponse response = autoPartService.createAutoPart(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Auto part created successfully", response));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<AutoPartResponse>> updateAutoPart(
            @PathVariable Long id,
            @Valid @RequestBody AutoPartRequest request,
            Authentication authentication) {
        
        AutoPartResponse response = autoPartService.updateAutoPart(id, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Auto part updated successfully", response));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AutoPartResponse>> getAutoPart(@PathVariable Long id) {
        AutoPartResponse response = autoPartService.getAutoPart(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<Page<AutoPartResponse>>> searchAutoParts(
            @RequestBody AutoPartSearchRequest request) {
        
        Page<AutoPartResponse> response = autoPartService.searchAutoParts(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<Page<AutoPartResponse>>> getFeaturedParts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<AutoPartResponse> response = autoPartService.getFeaturedParts(page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/my-parts")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<AutoPartResponse>>> getMyParts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        
        Page<AutoPartResponse> response = autoPartService.getMyParts(authentication.getName(), page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<String>> deleteAutoPart(
            @PathVariable Long id,
            Authentication authentication) {
        
        autoPartService.deleteAutoPart(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Auto part deleted successfully", "OK"));
    }
    
    @GetMapping("/brands")
    public ResponseEntity<ApiResponse<List<String>>> getAllBrands() {
        List<String> brands = autoPartService.getAllBrands();
        return ResponseEntity.ok(ApiResponse.success(brands));
    }
    
    @GetMapping("/vehicle-makes")
    public ResponseEntity<ApiResponse<List<String>>> getAllVehicleMakes() {
        List<String> makes = autoPartService.getAllVehicleMakes();
        return ResponseEntity.ok(ApiResponse.success(makes));
    }
    
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<AutoPartResponse>>> getAllPartsForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<AutoPartResponse> response = autoPartService.getAllPartsForAdmin(page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> approvePart(@PathVariable Long id) {
        autoPartService.approvePart(id);
        return ResponseEntity.ok(ApiResponse.success("Part approved successfully", "OK"));
    }
}
