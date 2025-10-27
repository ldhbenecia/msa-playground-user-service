package com.benecia.user_service.service;

import com.benecia.user_service.common.AppException;
import com.benecia.user_service.common.ErrorCode;
import com.benecia.user_service.dto.UserResponse;
import com.benecia.user_service.repository.UserEntity;
import com.benecia.user_service.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserReader {

    private final UserJpaRepository userRepository;

    public UserResponse getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "User not found for userId: " + userId));

        return new UserResponse(userEntity.getUserId(), userEntity.getName(), userEntity.getEmail());
    }
}
