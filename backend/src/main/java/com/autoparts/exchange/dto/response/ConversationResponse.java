package com.autoparts.exchange.dto.response;

import com.autoparts.exchange.entity.Conversation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {
    
    private Long id;
    private UserResponse buyer;
    private UserResponse seller;
    private AutoPartResponse autoPart;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private Integer unreadCount;
    private Boolean isActive;
    private LocalDateTime createdAt;
    
    public static ConversationResponse fromEntity(Conversation conversation, Long currentUserId) {
        // Determine unread count based on current user
        Integer unreadCount = 0;
        if (conversation.getBuyer().getId().equals(currentUserId)) {
            unreadCount = conversation.getBuyerUnreadCount();
        } else if (conversation.getSeller().getId().equals(currentUserId)) {
            unreadCount = conversation.getSellerUnreadCount();
        }
        
        return ConversationResponse.builder()
                .id(conversation.getId())
                .buyer(UserResponse.fromUser(conversation.getBuyer()))
                .seller(UserResponse.fromUser(conversation.getSeller()))
                .autoPart(conversation.getAutoPart() != null ? AutoPartResponse.fromEntity(conversation.getAutoPart()) : null)
                .lastMessage(conversation.getLastMessage())
                .lastMessageAt(conversation.getLastMessageAt())
                .unreadCount(unreadCount)
                .isActive(conversation.getIsActive())
                .createdAt(conversation.getCreatedAt())
                .build();
    }
}
