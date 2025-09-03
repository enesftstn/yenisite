package com.autoparts.exchange.repository.jpa;

import com.autoparts.exchange.entity.Conversation;
import com.autoparts.exchange.entity.Message;
import com.autoparts.exchange.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    Page<Message> findByConversationOrderByCreatedAtDesc(Conversation conversation, Pageable pageable);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation = :conversation AND m.recipient = :user AND m.isRead = false")
    Long countUnreadByConversationAndRecipient(@Param("conversation") Conversation conversation, @Param("user") User user);
    
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true, m.readAt = CURRENT_TIMESTAMP WHERE m.conversation = :conversation AND m.recipient = :user AND m.isRead = false")
    void markAllAsReadByConversationAndRecipient(@Param("conversation") Conversation conversation, @Param("user") User user);
    
    @Query("SELECT m FROM Message m WHERE m.conversation = :conversation ORDER BY m.createdAt DESC LIMIT 1")
    Message findLastMessageByConversation(@Param("conversation") Conversation conversation);
}
