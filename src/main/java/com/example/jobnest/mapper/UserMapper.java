package com.example.jobnest.mapper;

import com.example.jobnest.dto.response.UserResponse;
import com.example.jobnest.entity.Users;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between User entities and DTOs.
 */
@Component
public class UserMapper {

    /**
     * Convert User entity to UserResponse DTO.
     * Excludes sensitive information like passwords.
     */
    public UserResponse toResponse(Users user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .active(user.isActive())
                .registrationDate(user.getRegistrationDate())
                .userTypeName(user.getUserTypeId() != null ? user.getUserTypeId().getUserTypeName() : null)
                .userTypeId(user.getUserTypeId() != null ? user.getUserTypeId().getUserTypeId() : null)
                .build();
    }
}
