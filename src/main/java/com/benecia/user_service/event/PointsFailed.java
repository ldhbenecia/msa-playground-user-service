package com.benecia.user_service.event;

public record PointsFailed(
        Long orderId,
        String userId,
        String reason
) {
}
