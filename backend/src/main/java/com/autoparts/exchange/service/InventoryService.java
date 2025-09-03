package com.autoparts.exchange.service;

import com.autoparts.exchange.entity.AutoPart;
import com.autoparts.exchange.entity.OrderItem;
import com.autoparts.exchange.exception.BusinessException;
import com.autoparts.exchange.exception.ErrorCodes;
import com.autoparts.exchange.repository.jpa.AutoPartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    
    private final AutoPartRepository autoPartRepository;
    
    @Transactional
    @CacheEvict(value = "autoparts", key = "#orderItems.![autoPart.id]")
    public void reserveInventory(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            AutoPart autoPart = item.getAutoPart();
            
            if (!autoPart.getIsAvailable()) {
                throw new BusinessException(
                    ErrorCodes.AUTOPART_NOT_AVAILABLE,
                    "Auto part is no longer available: " + autoPart.getName()
                );
            }
            
            if (autoPart.getQuantity() < item.getQuantity()) {
                throw new BusinessException(
                    ErrorCodes.INSUFFICIENT_QUANTITY,
                    "Insufficient quantity for part: " + autoPart.getName() + 
                    ". Available: " + autoPart.getQuantity() + ", Requested: " + item.getQuantity()
                );
            }
            
            int newQuantity = autoPart.getQuantity() - item.getQuantity();
            autoPart.setQuantity(newQuantity);
            
            if (newQuantity <= 0) {
                autoPart.setIsAvailable(false);
                autoPart.setStatus(AutoPart.Status.SOLD);
            }
            
            autoPartRepository.save(autoPart);
            log.info("Reserved {} units of part {} (ID: {})", 
                item.getQuantity(), autoPart.getName(), autoPart.getId());
        }
    }
    
    @Transactional
    @CacheEvict(value = "autoparts", key = "#orderItems.![autoPart.id]")
    public void restoreInventory(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            AutoPart autoPart = item.getAutoPart();
            int newQuantity = autoPart.getQuantity() + item.getQuantity();
            
            autoPart.setQuantity(newQuantity);
            autoPart.setIsAvailable(true);
            
            if (autoPart.getStatus() == AutoPart.Status.SOLD) {
                autoPart.setStatus(AutoPart.Status.ACTIVE);
            }
            
            autoPartRepository.save(autoPart);
            log.info("Restored {} units of part {} (ID: {})", 
                item.getQuantity(), autoPart.getName(), autoPart.getId());
        }
    }
    
    @Cacheable(value = "inventory", key = "#autoPartId")
    public boolean isAvailable(Long autoPartId, int requestedQuantity) {
        AutoPart autoPart = autoPartRepository.findById(autoPartId)
            .orElseThrow(() -> new BusinessException(
                ErrorCodes.AUTOPART_NOT_FOUND,
                "Auto part not found with ID: " + autoPartId
            ));
            
        return autoPart.getIsAvailable() && autoPart.getQuantity() >= requestedQuantity;
    }
}
