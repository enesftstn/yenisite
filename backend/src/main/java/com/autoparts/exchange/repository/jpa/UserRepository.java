package com.autoparts.exchange.repository.jpa;

import com.autoparts.exchange.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Optional<User> findByVerificationToken(String token);
    
    Optional<User> findByResetToken(String token);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.isVerified = true")
    java.util.List<User> findAllActiveUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'USER' AND u.isActive = true")
    Long countActiveUsers();
}
