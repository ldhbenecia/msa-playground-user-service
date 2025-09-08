package com.benecia.user_service.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @Value("${health.message}")
    private String message;

    @GetMapping("/health")
    public String healthCheck() {
        return message;
    }
}
