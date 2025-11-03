package com.benecia.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 보호 비활성화 (Stateless API이므로)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. HTTP Basic 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)

                // 3. Form Login 비활성화
                .formLogin(AbstractHttpConfigurer::disable)

                // 4. 모든 요청(/**)을 인증 없이 허용 (permitAll)
                // K8s에서는 API Gateway가 인증을 담당하고, 내부 서비스 간 통신은
                // 네트워크 정책(NetworkPolicy)으로 제어하는 것이 일반적입니다.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll() // 모든 경로 허용
                );

        return http.build();
    }
}
