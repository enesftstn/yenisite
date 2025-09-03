package com.autoparts.exchange.dto.response;

import com.autoparts.exchange.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    
    private Long id;
    private Long conversationId;
    private UserResponse sender;
    private UserResponse recipient;
    private String content;
    private Message.MessageType type;
    private String attachmentUrl;
    private Boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
    
    public static MessageResponse fromEntity(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .sender(UserResponse.fromUser(message.getSender()))
                .recipient(UserResponse.fromUser(message.getRecipient()))
                .content(message.getContent())
                .type(message.getType())
                .attachmentUrl(message.getAttachmentUrl())
                .isRead(message.getIsRead())
                .readAt(message.getReadAt())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
