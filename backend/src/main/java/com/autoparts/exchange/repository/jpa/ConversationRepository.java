package com.autoparts.exchange.repository.jpa;

import com.autoparts.exchange.entity.AutoPart;
import com.autoparts.exchange.entity.Conversation;
import com.autoparts.exchange.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    
    @Query("SELECT c FROM Conversation c WHERE (c.buyer = :user OR c.seller = :user) AND c.isActive = true ORDER BY c.lastMessageAt DESC")
    Page<Conversation> findByUserOrderByLastMessageAtDesc(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT c FROM Conversation c WHERE c.buyer = :buyer AND c.seller = :seller AND c.autoPart = :autoPart")
    Optional<Conversation> findByBuyerAndSellerAndAutoPart(@Param("buyer") User buyer, 
                                                          @Param("seller") User seller, 
                                                          @Param("autoPart") AutoPart autoPart);
    
    @Query("SELECT c FROM Conversation c WHERE (c.buyer = :user OR c.seller = :user) AND c.id = :conversationId")
    Optional<Conversation> findByIdAndUser(@Param("conversationId") Long conversationId, @Param("user") User user);
    
    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.buyer = :user AND c.buyerUnreadCount > 0")
    Long countUnreadForBuyer(@Param("user") User user);
    
    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.seller = :user AND c.sellerUnreadCount > 0")
    Long countUnreadForSeller(@Param("user") User user);
}
