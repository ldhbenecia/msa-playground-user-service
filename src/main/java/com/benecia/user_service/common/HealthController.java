package com.benecia.user_service.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @Value("${health.message}")
    private String message;

    @GetMapping("/health")
    public String healthCheck(@RequestHeader(value = "X-Custom-Header", required = false) String customHeader) {
        System.out.println("Custom Header: " + customHeader); // 헤더 값 출력
        return message;
    }

    @GetMapping("/welcome")
    public String welcome(){
        return "Welcome User Service";
    }
}
