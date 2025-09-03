package com.autoparts.exchange.service;

import com.autoparts.exchange.dto.request.AddFavoriteRequest;
import com.autoparts.exchange.dto.response.FavoriteResponse;
import com.autoparts.exchange.entity.AutoPart;
import com.autoparts.exchange.entity.Favorite;
import com.autoparts.exchange.entity.User;
import com.autoparts.exchange.exception.ResourceNotFoundException;
import com.autoparts.exchange.repository.jpa.AutoPartRepository;
import com.autoparts.exchange.repository.jpa.FavoriteRepository;
import com.autoparts.exchange.repository.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteService {
    
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final AutoPartRepository autoPartRepository;
    
    @Transactional
    public FavoriteResponse addToFavorites(AddFavoriteRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        
        AutoPart autoPart = autoPartRepository.findById(request.getAutoPartId())
                .orElseThrow(() -> new ResourceNotFoundException("AutoPart", "id", request.getAutoPartId()));
        
        // Check if already favorited
        if (favoriteRepository.existsByUserAndAutoPart(user, autoPart)) {
            throw new RuntimeException("Auto part is already in favorites");
        }
        
        Favorite favorite = Favorite.builder()
                .user(user)
                .autoPart(autoPart)
                .build();
        
        Favorite savedFavorite = favoriteRepository.save(favorite);
        
        // Update favorite count on auto part
        Long favoriteCount = favoriteRepository.countByAutoPart(autoPart);
        autoPart.setFavoriteCount(favoriteCount);
        autoPartRepository.save(autoPart);
        
        log.info("Auto part {} added to favorites by user: {}", request.getAutoPartId(), userEmail);
        
        return FavoriteResponse.fromEntity(savedFavorite);
    }
    
    @Transactional
    public void removeFromFavorites(Long autoPartId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        
        AutoPart autoPart = autoPartRepository.findById(autoPartId)
                .orElseThrow(() -> new ResourceNotFoundException("AutoPart", "id", autoPartId));
        
        if (!favoriteRepository.existsByUserAndAutoPart(user, autoPart)) {
            throw new ResourceNotFoundException("Favorite not found");
        }
        
        favoriteRepository.deleteByUserAndAutoPart(user, autoPart);
        
        // Update favorite count on auto part
        Long favoriteCount = favoriteRepository.countByAutoPart(autoPart);
        autoPart.setFavoriteCount(favoriteCount);
        autoPartRepository.save(autoPart);
        
        log.info("Auto part {} removed from favorites by user: {}", autoPartId, userEmail);
    }
    
    public Page<FavoriteResponse> getFavorites(String userEmail, int page, int size) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Favorite> favorites = favoriteRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        
        return favorites.map(FavoriteResponse::fromEntity);
    }
    
    public boolean isFavorite(Long autoPartId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        
        AutoPart autoPart = autoPartRepository.findById(autoPartId)
                .orElseThrow(() -> new ResourceNotFoundException("AutoPart", "id", autoPartId));
        
        return favoriteRepository.existsByUserAndAutoPart(user, autoPart);
    }
    
    public Long getFavoriteCount(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        
        return favoriteRepository.countByUser(user);
    }
}
