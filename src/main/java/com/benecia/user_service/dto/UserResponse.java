package com.benecia.user_service.dto;

import com.benecia.user_service.repository.UserEntity;

public record UserResponse(
        String userId,
        String name,
        String email
) {
}
