package com.benecia.user_service.dto;

public record CreateUserRequest(
        String email,
        String name,
        String password
) {
}
