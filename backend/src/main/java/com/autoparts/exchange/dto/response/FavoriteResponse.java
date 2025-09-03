package com.autoparts.exchange.dto.response;

import com.autoparts.exchange.entity.Favorite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponse {
    
    private Long id;
    private AutoPartResponse autoPart;
    private LocalDateTime createdAt;
    
    public static FavoriteResponse fromEntity(Favorite favorite) {
        return FavoriteResponse.builder()
                .id(favorite.getId())
                .autoPart(AutoPartResponse.fromEntity(favorite.getAutoPart()))
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}
