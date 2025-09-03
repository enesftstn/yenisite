package com.autoparts.exchange.dto.request;

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
public class StartConversationRequest {
    
    @NotNull(message = "Auto part ID is required")
    private Long autoPartId;
    
    @NotBlank(message = "Initial message is required")
    private String initialMessage;
}
