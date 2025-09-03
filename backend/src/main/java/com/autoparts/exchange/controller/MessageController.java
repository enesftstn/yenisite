package com.autoparts.exchange.controller;

import com.autoparts.exchange.dto.request.MessageRequest;
import com.autoparts.exchange.dto.request.StartConversationRequest;
import com.autoparts.exchange.dto.response.ApiResponse;
import com.autoparts.exchange.dto.response.ConversationResponse;
import com.autoparts.exchange.dto.response.MessageResponse;
import com.autoparts.exchange.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MessageController {
    
    private final MessageService messageService;
    
    @PostMapping("/conversations")
    public ResponseEntity<ApiResponse<ConversationResponse>> startConversation(
            @Valid @RequestBody StartConversationRequest request,
            Authentication authentication) {
        
        ConversationResponse response = messageService.startConversation(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Conversation started successfully", response));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @Valid @RequestBody MessageRequest request,
            Authentication authentication) {
        
        MessageResponse response = messageService.sendMessage(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Message sent successfully", response));
    }
    
    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<Page<ConversationResponse>>> getConversations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        
        Page<ConversationResponse> response = messageService.getConversations(authentication.getName(), page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<ApiResponse<Page<MessageResponse>>> getMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Authentication authentication) {
        
        Page<MessageResponse> response = messageService.getMessages(conversationId, authentication.getName(), page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PutMapping("/conversations/{conversationId}/read")
    public ResponseEntity<ApiResponse<String>> markMessagesAsRead(
            @PathVariable Long conversationId,
            Authentication authentication) {
        
        messageService.markMessagesAsRead(conversationId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Messages marked as read", "OK"));
    }
    
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadMessageCount(Authentication authentication) {
        Long count = messageService.getUnreadMessageCount(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
