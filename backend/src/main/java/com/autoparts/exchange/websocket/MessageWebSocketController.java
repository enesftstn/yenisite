package com.autoparts.exchange.websocket;

import com.autoparts.exchange.dto.request.MessageRequest;
import com.autoparts.exchange.dto.response.MessageResponse;
import com.autoparts.exchange.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MessageWebSocketController {
    
    private final MessageService messageService;
    
    @MessageMapping("/chat.sendMessage")
    public MessageResponse sendMessage(@Payload MessageRequest messageRequest, 
                                     SimpMessageHeaderAccessor headerAccessor,
                                     Principal principal) {
        
        try {
            String senderEmail = principal.getName();
            MessageResponse response = messageService.sendMessage(messageRequest, senderEmail);
            
            log.info("WebSocket message sent from: {} in conversation: {}", 
                    senderEmail, messageRequest.getConversationId());
            
            return response;
        } catch (Exception e) {
            log.error("Error sending WebSocket message: {}", e.getMessage());
            throw e;
        }
    }
}
