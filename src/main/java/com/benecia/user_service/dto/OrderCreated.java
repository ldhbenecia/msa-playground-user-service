package com.benecia.user_service.dto;

import java.time.LocalDateTime;

public record OrderCreated(
        Long orderId,
        String productId,
        Integer qty,
        Integer unitPrice,
        Integer totalPrice,
        String userId,
        LocalDateTime createdAt
) {
}
