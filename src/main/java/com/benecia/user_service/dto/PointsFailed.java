package com.benecia.user_service.dto;

public record PointsFailed(
        Long orderId,
        String userId,
        String reason
) {
}
