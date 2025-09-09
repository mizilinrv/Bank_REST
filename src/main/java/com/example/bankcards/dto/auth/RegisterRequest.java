package com.example.bankcards.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.Pattern;

/**
 * Data Transfer Object (DTO) representing a user registration request.
 * <p>
 * Contains the information required to register a new user in the system.
 * </p>
 */
@Data
@AllArgsConstructor
public class RegisterRequest {

    /**
     * Minimum allowed length for the user's full name.
     */
    public static final int FULL_NAME_MIN_LENGTH = 2;

    /**
     * Maximum allowed length for the user's full name.
     */
    public static final int FULL_NAME_MAX_LENGTH = 100;

    /**
     * Minimum allowed length for the user's password.
     */
    public static final int PASSWORD_MIN_LENGTH = 8;

    /**
     * Maximum allowed length for the user's password.
     */
    public static final int PASSWORD_MAX_LENGTH = 100;

    /**
     * Full name of the user.
     * <p>
     * Must be between 2 and 100 characters and cannot be blank.
     * </p>
     */
    @NotBlank
    @Size(min = FULL_NAME_MIN_LENGTH, max = FULL_NAME_MAX_LENGTH)
    private String fullName;

    /**
     * Email address of the user.
     * <p>
     * Must be a valid email format and cannot be blank.
     * </p>
     */
    @Email
    @NotBlank
    private String email;

    /**
     * Phone number of the user.
     * <p>
     * Must match the pattern of an optional '+' followed by 7 to 20 digits.
     * </p>
     */
    @Pattern(regexp = "\\+?\\d{7,20}")
    private String phoneNumber;

    /**
     * Password for the user's account.
     * <p>
     * Must be between 8 and 100 characters and cannot be blank.
     * </p>
     */
    @NotBlank
    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;
}
