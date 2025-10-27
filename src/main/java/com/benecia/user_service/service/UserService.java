package com.benecia.user_service.service;

import com.benecia.user_service.dto.CreateUserRequest;
import com.benecia.user_service.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserReader userReader;
    private final UserWriter userWriter;

    public UserResponse getUser(String userId) {
        return userReader.getUserByUserId(userId);
    }

    public UserResponse createUser(CreateUserRequest requestDto) {
        return userWriter.createUser(requestDto);
    }
}
