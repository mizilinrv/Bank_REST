package com.example.bankcards.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for updating user information.
 * Contains fields that can be updated for an existing user.
 */
@Data
@AllArgsConstructor
public class UserUpdateRequest {

    /**
     * New full name of the user.
     */
    private String fullName;

    /**
     * New phone number of the user.
     */
    private String phoneNumber;

    /**
     * New password for the user.
     */
    private String password;
}
