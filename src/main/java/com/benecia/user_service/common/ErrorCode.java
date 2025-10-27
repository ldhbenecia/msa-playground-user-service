package com.benecia.user_service.common;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    EMAIL_ALREADY_IN_USE(HttpStatus.CONFLICT, "Email already in use"),
    USER_INACTIVE(HttpStatus.FORBIDDEN, "User is inactive"),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "Validation failed");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus status() { return status; }
    public String defaultMessage() { return defaultMessage; }
}
