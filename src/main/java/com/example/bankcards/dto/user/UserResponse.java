package com.example.bankcards.dto.user;

import com.example.bankcards.entity.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO representing the public response for a user.
 * Contains information about the user's profile and role.
 */
@Data
@AllArgsConstructor
public class UserResponse {

    /**
     * Unique identifier of the user.
     */
    private Long id;

    /**
     * Full name of the user.
     */
    private String fullName;

    /**
     * Email address of the user.
     */
    private String email;

    /**
     * Phone number of the user.
     */
    private String phoneNumber;

    /**
     * Role assigned to the user.
     */
    private RoleType role;

    /**
     * Date and time when the user was created.
     */
    private LocalDateTime createdAt;
}
