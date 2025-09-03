package com.autoparts.exchange.controller;

import com.autoparts.exchange.dto.request.CreateOrderRequest;
import com.autoparts.exchange.dto.request.UpdateOrderStatusRequest;
import com.autoparts.exchange.dto.response.ApiResponse;
import com.autoparts.exchange.dto.response.OrderResponse;
import com.autoparts.exchange.entity.Order;
import com.autoparts.exchange.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication) {
        
        OrderResponse response = orderService.createOrder(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", response));
    }
    
    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            Authentication authentication) {
        
        OrderResponse response = orderService.updateOrderStatus(orderId, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", response));
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @PathVariable Long orderId,
            Authentication authentication) {
        
        OrderResponse response = orderService.getOrder(orderId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(
            @RequestParam String type, // "purchases" or "sales"
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        
        Page<OrderResponse> response = orderService.getMyOrders(authentication.getName(), type, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/my-orders/status/{status}")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getOrdersByStatus(
            @PathVariable Order.OrderStatus status,
            @RequestParam String type, // "purchases" or "sales"
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        
        Page<OrderResponse> response = orderService.getOrdersByStatus(authentication.getName(), type, status, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
