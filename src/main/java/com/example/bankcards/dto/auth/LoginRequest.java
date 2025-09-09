package com.example.bankcards.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a login request.
 * <p>
 * Contains the credentials required for user authentication.
 * </p>
 */
@Data
@AllArgsConstructor
public class LoginRequest {

    /**
     * User's email address used for login.
     * <p>
     * Must be a valid email format and cannot be blank.
     * </p>
     */
    @Email
    @NotBlank
    private String email;

    /**
     * User's password used for login.
     * <p>
     * Cannot be blank.
     * </p>
     */
    @NotBlank
    private String password;
}
