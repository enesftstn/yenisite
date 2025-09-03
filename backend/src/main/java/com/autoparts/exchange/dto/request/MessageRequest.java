package com.autoparts.exchange.dto.request;

import com.autoparts.exchange.entity.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {
    
    @NotNull(message = "Conversation ID is required")
    private Long conversationId;
    
    @NotBlank(message = "Message content is required")
    private String content;
    
    private Message.MessageType type = Message.MessageType.TEXT;
    
    private String attachmentUrl;
}
