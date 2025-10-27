package com.benecia.user_service.service;

import com.benecia.user_service.dto.CreateUserRequest;
import com.benecia.user_service.dto.UserResponse;
import com.benecia.user_service.repository.UserEntity;
import com.benecia.user_service.repository.UserJpaRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserWriter {

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(CreateUserRequest request) {
        UserEntity userEntity = new UserEntity(
                request.email(),
                request.name(),
                UUID.randomUUID().toString(),
                passwordEncoder.encode(request.password())
        );
        userJpaRepository.save(userEntity);

        return new UserResponse(userEntity.getUserId(), userEntity.getName(), userEntity.getEmail());
    }
}
