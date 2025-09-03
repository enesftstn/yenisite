package com.autoparts.exchange.repository.jpa;

import com.autoparts.exchange.entity.AutoPart;
import com.autoparts.exchange.entity.Favorite;
import com.autoparts.exchange.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    Page<Favorite> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    Optional<Favorite> findByUserAndAutoPart(User user, AutoPart autoPart);
    
    boolean existsByUserAndAutoPart(User user, AutoPart autoPart);
    
    void deleteByUserAndAutoPart(User user, AutoPart autoPart);
    
    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.autoPart = :autoPart")
    Long countByAutoPart(@Param("autoPart") AutoPart autoPart);
    
    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.user = :user")
    Long countByUser(@Param("user") User user);
}
