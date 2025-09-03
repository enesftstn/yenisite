package com.autoparts.exchange.service;

import com.autoparts.exchange.dto.request.CreateOrderRequest;
import com.autoparts.exchange.dto.request.UpdateOrderStatusRequest;
import com.autoparts.exchange.dto.response.OrderResponse;
import com.autoparts.exchange.entity.*;
import com.autoparts.exchange.exception.BusinessException;
import com.autoparts.exchange.exception.ResourceNotFoundException;
import com.autoparts.exchange.repository.jpa.*;
import com.autoparts.exchange.service.inventory.InventoryService;
import com.autoparts.exchange.service.order.OrderCalculationService;
import com.autoparts.exchange.service.user.UserStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final AutoPartRepository autoPartRepository;
    private final PaymentService paymentService;
    private final InventoryService inventoryService;
    private final OrderCalculationService calculationService;
    private final UserStatisticsService statisticsService;
    
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public OrderResponse createOrder(CreateOrderRequest request, String buyerEmail) {
        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new BusinessException(ErrorCodes.USER_NOT_FOUND, "User not found", buyerEmail));
        
        List<OrderItem> orderItems = validateAndBuildOrderItems(request, buyer);
        Order.OrderTotals totals = calculationService.calculateOrderTotals(
            orderItems, request.getShippingZipCode(), request.getShippingState()
        );
        
        Order order = buildOrder(request, buyer, orderItems.get(0).getAutoPart().getSeller(), totals);
        Order savedOrder = orderRepository.save(order);
        
        // Save order items
        orderItems.forEach(item -> {
            item.setOrder(savedOrder);
            orderItemRepository.save(item);
        });
        
        try {
            paymentService.processPayment(savedOrder, request.getPaymentMethod());
            inventoryService.reserveInventory(orderItems);
            
            savedOrder.setStatus(Order.OrderStatus.CONFIRMED);
            savedOrder.setPaymentStatus(Order.PaymentStatus.COMPLETED);
            savedOrder.setConfirmedAt(LocalDateTime.now());
            orderRepository.save(savedOrder);
            
            statisticsService.updateBuyerStatistics(buyer);
            statisticsService.updateSellerStatistics(orderItems.get(0).getAutoPart().getSeller());
            
            log.info("Order created successfully: {} for buyer: {}", savedOrder.getOrderNumber(), buyerEmail);
            
        } catch (Exception e) {
            log.error("Order processing failed for order: {}", savedOrder.getOrderNumber(), e);
            savedOrder.setStatus(Order.OrderStatus.CANCELLED);
            savedOrder.setPaymentStatus(Order.PaymentStatus.FAILED);
            orderRepository.save(savedOrder);
            
            throw new BusinessException(ErrorCodes.PAYMENT_FAILED, "Order processing failed", e.getMessage());
        }
        
        return OrderResponse.fromEntity(savedOrder);
    }
    
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request, String sellerEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        // Verify seller owns this order
        if (!order.getSeller().getEmail().equals(sellerEmail)) {
            throw new RuntimeException("You can only update your own orders");
        }
        
        Order.OrderStatus previousStatus = order.getStatus();
        order.setStatus(request.getStatus());
        order.setSellerNotes(request.getSellerNotes());
        
        // Update timestamps based on status
        switch (request.getStatus()) {
            case SHIPPED:
                if (previousStatus != Order.OrderStatus.SHIPPED) {
                    order.setShippedAt(LocalDateTime.now());
                    order.setTrackingNumber(request.getTrackingNumber());
                    order.setShippingCarrier(request.getShippingCarrier());
                    // Estimate delivery (simplified - 3-7 days)
                    order.setEstimatedDelivery(LocalDateTime.now().plusDays(5));
                }
                break;
            case DELIVERED:
                if (previousStatus != Order.OrderStatus.DELIVERED) {
                    order.setDeliveredAt(LocalDateTime.now());
                }
                break;
            case CANCELLED:
                if (previousStatus != Order.OrderStatus.CANCELLED) {
                    order.setCancelledAt(LocalDateTime.now());
                    // Restore inventory
                    restoreInventory(order.getOrderItems());
                }
                break;
        }
        
        Order savedOrder = orderRepository.save(order);
        
        log.info("Order status updated: {} from {} to {} by seller: {}", 
                order.getOrderNumber(), previousStatus, request.getStatus(), sellerEmail);
        
        return OrderResponse.fromEntity(savedOrder);
    }
    
    public OrderResponse getOrder(Long orderId, String userEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        // Verify user has access to this order
        if (!order.getBuyer().getEmail().equals(userEmail) && 
            !order.getSeller().getEmail().equals(userEmail)) {
            throw new RuntimeException("You don't have access to this order");
        }
        
        return OrderResponse.fromEntity(order);
    }
    
    public Page<OrderResponse> getMyOrders(String userEmail, String type, int page, int size) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders;
        
        switch (type.toLowerCase()) {
            case "purchases":
                orders = orderRepository.findByBuyerOrderByCreatedAtDesc(user, pageable);
                break;
            case "sales":
                orders = orderRepository.findBySellerOrderByCreatedAtDesc(user, pageable);
                break;
            default:
                throw new RuntimeException("Invalid order type. Use 'purchases' or 'sales'");
        }
        
        return orders.map(OrderResponse::fromEntity);
    }
    
    public Page<OrderResponse> getOrdersByStatus(String userEmail, String type, Order.OrderStatus status, int page, int size) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders;
        
        switch (type.toLowerCase()) {
            case "purchases":
                orders = orderRepository.findByBuyerAndStatusOrderByCreatedAtDesc(user, status, pageable);
                break;
            case "sales":
                orders = orderRepository.findBySellerAndStatusOrderByCreatedAtDesc(user, status, pageable);
                break;
            default:
                throw new RuntimeException("Invalid order type. Use 'purchases' or 'sales'");
        }
        
        return orders.map(OrderResponse::fromEntity);
    }
    
    private List<OrderItem> validateAndBuildOrderItems(CreateOrderRequest request, User buyer) {
        List<OrderItem> orderItems = new ArrayList<>();
        User seller = null;
        
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            AutoPart autoPart = autoPartRepository.findById(itemRequest.getAutoPartId())
                    .orElseThrow(() -> new BusinessException(
                        ErrorCodes.AUTOPART_NOT_FOUND, 
                        "Auto part not found", 
                        itemRequest.getAutoPartId()
                    ));
            
            if (!inventoryService.isAvailable(autoPart.getId(), itemRequest.getQuantity())) {
                throw new BusinessException(
                    ErrorCodes.AUTOPART_NOT_AVAILABLE,
                    "Auto part is not available in requested quantity",
                    autoPart.getName()
                );
            }
            
            // Validate single seller per order
            if (seller == null) {
                seller = autoPart.getSeller();
            } else if (!seller.getId().equals(autoPart.getSeller().getId())) {
                throw new BusinessException(
                    ErrorCodes.INVALID_INPUT,
                    "All items must be from the same seller"
                );
            }
            
            BigDecimal itemTotal = autoPart.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            
            OrderItem orderItem = OrderItem.builder()
                    .autoPart(autoPart)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(autoPart.getPrice())
                    .totalPrice(itemTotal)
                    .partName(autoPart.getName())
                    .partBrand(autoPart.getBrand())
                    .partNumber(autoPart.getPartNumber())
                    .partCondition(autoPart.getCondition().name())
                    .build();
            
            orderItems.add(orderItem);
        }
        
        return orderItems;
    }
    
    private Order buildOrder(CreateOrderRequest request, User buyer, User seller, Order.OrderTotals totals) {
        return Order.builder()
                .buyer(buyer)
                .seller(seller)
                .subtotal(totals.getSubtotal())
                .shippingCost(totals.getShippingCost())
                .taxAmount(totals.getTaxAmount())
                .totalAmount(totals.getTotalAmount())
                .status(Order.OrderStatus.PENDING)
                .paymentStatus(Order.PaymentStatus.PENDING)
                .shippingAddress(request.getShippingAddress())
                .shippingCity(request.getShippingCity())
                .shippingState(request.getShippingState())
                .shippingZipCode(request.getShippingZipCode())
                .shippingCountry(request.getShippingCountry())
                .shippingPhone(request.getShippingPhone())
                .buyerNotes(request.getBuyerNotes())
                .build();
    }
    
    private void restoreInventory(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            AutoPart autoPart = item.getAutoPart();
            int newQuantity = autoPart.getQuantity() + item.getQuantity();
            
            autoPart.setQuantity(newQuantity);
            autoPart.setIsAvailable(true);
            if (autoPart.getStatus() == AutoPart.Status.SOLD) {
                autoPart.setStatus(AutoPart.Status.ACTIVE);
            }
            
            autoPartRepository.save(autoPart);
        }
    }
}
