package com.autoparts.exchange.service;

import com.autoparts.exchange.document.AutoPartDocument;
import com.autoparts.exchange.dto.request.AutoPartRequest;
import com.autoparts.exchange.dto.request.AutoPartSearchRequest;
import com.autoparts.exchange.dto.response.AutoPartResponse;
import com.autoparts.exchange.entity.AutoPart;
import com.autoparts.exchange.entity.User;
import com.autoparts.exchange.exception.ResourceNotFoundException;
import com.autoparts.exchange.repository.elasticsearch.AutoPartSearchRepository;
import com.autoparts.exchange.repository.jpa.AutoPartRepository;
import com.autoparts.exchange.repository.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoPartService {
    
    private final AutoPartRepository autoPartRepository;
    private final AutoPartSearchRepository autoPartSearchRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public AutoPartResponse createAutoPart(AutoPartRequest request, String userEmail) {
        User seller = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        
        AutoPart autoPart = AutoPart.builder()
                .name(request.getName())
                .description(request.getDescription())
                .brand(request.getBrand())
                .vehicleMake(request.getVehicleMake())
                .vehicleModel(request.getVehicleModel())
                .vehicleYear(request.getVehicleYear())
                .partNumber(request.getPartNumber())
                .oemNumber(request.getOemNumber())
                .category(request.getCategory())
                .condition(request.getCondition())
                .price(request.getPrice())
                .originalPrice(request.getOriginalPrice())
                .quantity(request.getQuantity())
                .imageUrls(request.getImageUrls())
                .seller(seller)
                .location(request.getLocation())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .country(request.getCountry())
                .engineType(request.getEngineType())
                .transmissionType(request.getTransmissionType())
                .color(request.getColor())
                .material(request.getMaterial())
                .weight(request.getWeight())
                .dimensions(request.getDimensions())
                .status(AutoPart.Status.ACTIVE)
                .build();
        
        AutoPart savedPart = autoPartRepository.save(autoPart);
        
        // Index in Elasticsearch
        AutoPartDocument document = AutoPartDocument.fromEntity(savedPart);
        autoPartSearchRepository.save(document);
        
        log.info("Auto part created: {} by user: {}", savedPart.getId(), userEmail);
        
        return AutoPartResponse.fromEntity(savedPart);
    }
    
    @Transactional
    public AutoPartResponse updateAutoPart(Long id, AutoPartRequest request, String userEmail) {
        AutoPart autoPart = autoPartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AutoPart", "id", id));
        
        // Check if user owns this part
        if (!autoPart.getSeller().getEmail().equals(userEmail)) {
            throw new RuntimeException("You can only update your own parts");
        }
        
        // Update fields
        autoPart.setName(request.getName());
        autoPart.setDescription(request.getDescription());
        autoPart.setBrand(request.getBrand());
        autoPart.setVehicleMake(request.getVehicleMake());
        autoPart.setVehicleModel(request.getVehicleModel());
        autoPart.setVehicleYear(request.getVehicleYear());
        autoPart.setPartNumber(request.getPartNumber());
        autoPart.setOemNumber(request.getOemNumber());
        autoPart.setCategory(request.getCategory());
        autoPart.setCondition(request.getCondition());
        autoPart.setPrice(request.getPrice());
        autoPart.setOriginalPrice(request.getOriginalPrice());
        autoPart.setQuantity(request.getQuantity());
        autoPart.setImageUrls(request.getImageUrls());
        autoPart.setLocation(request.getLocation());
        autoPart.setCity(request.getCity());
        autoPart.setState(request.getState());
        autoPart.setZipCode(request.getZipCode());
        autoPart.setCountry(request.getCountry());
        autoPart.setEngineType(request.getEngineType());
        autoPart.setTransmissionType(request.getTransmissionType());
        autoPart.setColor(request.getColor());
        autoPart.setMaterial(request.getMaterial());
        autoPart.setWeight(request.getWeight());
        autoPart.setDimensions(request.getDimensions());
        
        AutoPart savedPart = autoPartRepository.save(autoPart);
        
        // Update in Elasticsearch
        AutoPartDocument document = AutoPartDocument.fromEntity(savedPart);
        autoPartSearchRepository.save(document);
        
        log.info("Auto part updated: {} by user: {}", savedPart.getId(), userEmail);
        
        return AutoPartResponse.fromEntity(savedPart);
    }
    
    public AutoPartResponse getAutoPart(Long id) {
        AutoPart autoPart = autoPartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AutoPart", "id", id));
        
        // Increment view count
        autoPart.setViewCount(autoPart.getViewCount() + 1);
        autoPartRepository.save(autoPart);
        
        return AutoPartResponse.fromEntity(autoPart);
    }
    
    public Page<AutoPartResponse> searchAutoParts(AutoPartSearchRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy())
        );
        
        Page<AutoPartDocument> documents;
        
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            documents = autoPartSearchRepository.searchByKeyword(request.getKeyword(), pageable);
        } else {
            documents = autoPartSearchRepository.findByStatusAndIsAvailableOrderByCreatedAtDesc("ACTIVE", true, pageable);
        }
        
        return documents.map(doc -> {
            AutoPart autoPart = autoPartRepository.findById(Long.parseLong(doc.getId()))
                    .orElse(null);
            return autoPart != null ? AutoPartResponse.fromEntity(autoPart) : null;
        }).map(response -> response);
    }
    
    public Page<AutoPartResponse> getFeaturedParts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AutoPart> parts = autoPartRepository.findByIsFeaturedTrueAndStatusAndIsAvailableOrderByCreatedAtDesc(
                AutoPart.Status.ACTIVE, true, pageable);
        
        return parts.map(AutoPartResponse::fromEntity);
    }
    
    public Page<AutoPartResponse> getMyParts(String userEmail, int page, int size) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AutoPart> parts = autoPartRepository.findBySellerAndStatusOrderByCreatedAtDesc(
                user, AutoPart.Status.ACTIVE, pageable);
        
        return parts.map(AutoPartResponse::fromEntity);
    }
    
    @Transactional
    public void deleteAutoPart(Long id, String userEmail) {
        AutoPart autoPart = autoPartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AutoPart", "id", id));
        
        // Check if user owns this part
        if (!autoPart.getSeller().getEmail().equals(userEmail)) {
            throw new RuntimeException("You can only delete your own parts");
        }
        
        // Soft delete - change status instead of actual deletion
        autoPart.setStatus(AutoPart.Status.SUSPENDED);
        autoPart.setIsAvailable(false);
        autoPartRepository.save(autoPart);
        
        // Remove from Elasticsearch
        autoPartSearchRepository.deleteById(id.toString());
        
        log.info("Auto part deleted: {} by user: {}", id, userEmail);
    }
    
    public List<String> getAllBrands() {
        return autoPartRepository.findAllBrands();
    }
    
    public List<String> getAllVehicleMakes() {
        return autoPartRepository.findAllVehicleMakes();
    }
}
