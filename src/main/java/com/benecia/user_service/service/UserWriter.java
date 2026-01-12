package com.benecia.user_service.service;

import com.benecia.user_service.common.AppException;
import com.benecia.user_service.common.ErrorCode;
import com.benecia.user_service.dto.CreateUserRequest;
import com.benecia.user_service.dto.LoginUserRequest;
import com.benecia.user_service.dto.UserResponse;
import com.benecia.user_service.repository.UserEntity;
import com.benecia.user_service.repository.UserJpaRepository;
import com.benecia.user_service.util.JwtUtil;
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
    private final JwtUtil jwtUtil;

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
        // [Test] SAGA Rollback 테스트를 위한 강제 예외 발생
        if ("VILLAIN".equals(userId)) {
            log.error("💣 User Service: 으악! 빌런이다! (DLQ 테스트)");

            // 주의: 이 예외는 Consumer의 try-catch 블록에서 잡히므로,
            // Kafka 레벨의 재시도(Retry)나 DLQ로 이동하지 않음.
            // 대신 catch 블록에서 'points-failed' 이벤트를 발행하여 즉시 SAGA 보상 트랜잭션(Rollback)을 수행함.
            throw new RuntimeException("User Service Error Triggered!");
        }

        UserEntity userEntity = userJpaRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "User not found for userId: " + userId));

        userEntity.addPoints(pointsToAdd);
        userJpaRepository.save(userEntity);
        log.info("Points added successfully for user: {}. Total points: {}", userId, userEntity.getPoints());
    }

    @Transactional
    public void refundPoints(String userId, int pointsToRefund) {
        UserEntity userEntity = userJpaRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "User not found for userId: " + userId));

        userEntity.refundPoints(pointsToRefund);
        userJpaRepository.save(userEntity);
        log.info("Points refunded successfully for user: {}. Total points: {}", userId, userEntity.getPoints());
    }

    public String login(LoginUserRequest request) {
        UserEntity user = userJpaRepository.findByEmail(request.email())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "User not found for email: " + request.email()));

        if (!passwordEncoder.matches(request.password(), user.getEncryptedPwd())) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Invalid password");
        }

        return jwtUtil.generateToken(user.getUserId());
    }
}
