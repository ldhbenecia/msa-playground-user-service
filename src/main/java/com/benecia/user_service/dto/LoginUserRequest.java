package com.benecia.user_service.dto;

public record LoginUserRequest(
        String email,
        String password
) {
}
