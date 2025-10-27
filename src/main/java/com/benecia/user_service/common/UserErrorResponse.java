package com.benecia.user_service.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserErrorResponse {
    private final int status;
    private final String error;
    private final String message;
    private final String path;

    public UserErrorResponse(HttpStatus status, String message, String path) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.path = path;
    }
}
