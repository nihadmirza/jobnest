package com.example.jobnest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DTO for user information responses.
 * Hides sensitive information like passwords.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Integer userId;
    private String email;
    private Boolean active;
    private Date registrationDate;
    private String userTypeName;
    private Integer userTypeId;
}
