package com.autoparts.exchange.service;

import com.autoparts.exchange.dto.request.MessageRequest;
import com.autoparts.exchange.dto.request.StartConversationRequest;
import com.autoparts.exchange.dto.response.ConversationResponse;
import com.autoparts.exchange.dto.response.MessageResponse;
import com.autoparts.exchange.entity.*;
import com.autoparts.exchange.exception.ResourceNotFoundException;
import com.autoparts.exchange.repository.jpa.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final AutoPartRepository autoPartRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
    @Transactional
    public ConversationResponse startConversation(StartConversationRequest request, String buyerEmail) {
        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", buyerEmail));
        
        AutoPart autoPart = autoPartRepository.findById(request.getAutoPartId())
                .orElseThrow(() -> new ResourceNotFoundException("AutoPart", "id", request.getAutoPartId()));
        
        User seller = autoPart.getSeller();
        
        // Check if conversation already exists
        Conversation existingConversation = conversationRepository
                .findByBuyerAndSellerAndAutoPart(buyer, seller, autoPart)
                .orElse(null);
        
        if (existingConversation != null) {
            return ConversationResponse.fromEntity(existingConversation, buyer.getId());
        }
        
        // Create new conversation
        Conversation conversation = Conversation.builder()
                .buyer(buyer)
                .seller(seller)
                .autoPart(autoPart)
                .lastMessage(request.getInitialMessage())
                .lastMessageAt(LocalDateTime.now())
                .buyerUnreadCount(0)
                .sellerUnreadCount(1) // Seller has unread message
                .build();
        
        Conversation savedConversation = conversationRepository.save(conversation);
        
        // Create initial message
        Message message = Message.builder()
                .conversation(savedConversation)
                .sender(buyer)
                .recipient(seller)
                .content(request.getInitialMessage())
                .type(Message.MessageType.TEXT)
                .build();
        
        messageRepository.save(message);
        
        // Send real-time notification to seller
        MessageResponse messageResponse = MessageResponse.fromEntity(message);
        messagingTemplate.convertAndSendToUser(
                seller.getEmail(),
                "/queue/messages",
                messageResponse
        );
        
        log.info("Conversation started between buyer: {} and seller: {} for part: {}", 
                buyerEmail, seller.getEmail(), autoPart.getId());
        
        return ConversationResponse.fromEntity(savedConversation, buyer.getId());
    }
    
    @Transactional
    public MessageResponse sendMessage(MessageRequest request, String senderEmail) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", senderEmail));
        
        Conversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", request.getConversationId()));
        
        // Verify user is part of this conversation
        if (!conversation.getBuyer().getId().equals(sender.getId()) && 
            !conversation.getSeller().getId().equals(sender.getId())) {
            throw new RuntimeException("You are not part of this conversation");
        }
        
        // Determine recipient
        User recipient = conversation.getBuyer().getId().equals(sender.getId()) 
                ? conversation.getSeller() 
                : conversation.getBuyer();
        
        // Create message
        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .recipient(recipient)
                .content(request.getContent())
                .type(request.getType())
                .attachmentUrl(request.getAttachmentUrl())
                .build();
        
        Message savedMessage = messageRepository.save(message);
        
        // Update conversation
        conversation.setLastMessage(request.getContent());
        conversation.setLastMessageAt(LocalDateTime.now());
        
        // Update unread counts
        if (conversation.getBuyer().getId().equals(sender.getId())) {
            conversation.setSellerUnreadCount(conversation.getSellerUnreadCount() + 1);
        } else {
            conversation.setBuyerUnreadCount(conversation.getBuyerUnreadCount() + 1);
        }
        
        conversationRepository.save(conversation);
        
        // Send real-time notification
        MessageResponse messageResponse = MessageResponse.fromEntity(savedMessage);
        messagingTemplate.convertAndSendToUser(
                recipient.getEmail(),
                "/queue/messages",
                messageResponse
        );
        
        log.info("Message sent from: {} to: {} in conversation: {}", 
                senderEmail, recipient.getEmail(), conversation.getId());
        
        return messageResponse;
    }
    
    public Page<ConversationResponse> getConversations(String userEmail, int page, int size) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Conversation> conversations = conversationRepository.findByUserOrderByLastMessageAtDesc(user, pageable);
        
        return conversations.map(conversation -> ConversationResponse.fromEntity(conversation, user.getId()));
    }
    
    public Page<MessageResponse> getMessages(Long conversationId, String userEmail, int page, int size) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        
        Conversation conversation = conversationRepository.findByIdAndUser(conversationId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messageRepository.findByConversationOrderByCreatedAtDesc(conversation, pageable);
        
        return messages.map(MessageResponse::fromEntity);
    }
    
    @Transactional
    public void markMessagesAsRead(Long conversationId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        
        Conversation conversation = conversationRepository.findByIdAndUser(conversationId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));
        
        // Mark messages as read
        messageRepository.markAllAsReadByConversationAndRecipient(conversation, user);
        
        // Reset unread count
        if (conversation.getBuyer().getId().equals(user.getId())) {
            conversation.setBuyerUnreadCount(0);
        } else {
            conversation.setSellerUnreadCount(0);
        }
        
        conversationRepository.save(conversation);
        
        log.info("Messages marked as read for user: {} in conversation: {}", userEmail, conversationId);
    }
    
    public Long getUnreadMessageCount(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        
        Long buyerUnread = conversationRepository.countUnreadForBuyer(user);
        Long sellerUnread = conversationRepository.countUnreadForSeller(user);
        
        return buyerUnread + sellerUnread;
    }
}
