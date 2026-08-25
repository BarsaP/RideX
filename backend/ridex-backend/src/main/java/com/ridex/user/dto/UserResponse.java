package com.ridex.user.dto;

import com.ridex.user.User;
import com.ridex.user.UserRepository;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String phone,
        String name,
        String email,
        String status,
        Instant createdAt
) {
    public static UserResponse from(User user){
        return new UserResponse(
                user.getId(),
                user.getPhone(),
                user.getName(),
                user.getEmail(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }

}