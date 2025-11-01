package com.benecia.user_service.service;

import com.benecia.user_service.common.AppException;
import com.benecia.user_service.common.ErrorCode;
import com.benecia.user_service.dto.CreateUserRequest;
import com.benecia.user_service.dto.UserResponse;
import com.benecia.user_service.repository.UserEntity;
import com.benecia.user_service.repository.UserJpaRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserWriter {

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
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

    @Transactional
    public void addPoints(String userId, int pointsToAdd) {
        UserEntity userEntity = userJpaRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "User not found for userId: " + userId));

        userEntity.addPoints(pointsToAdd);
        userJpaRepository.save(userEntity);
        log.info("Points added successfully for user: {}. Total points: {}", userId, userEntity.getPoints());
    }
}
