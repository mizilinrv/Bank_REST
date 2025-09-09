package com.example.bankcards.dto.user;

import com.example.bankcards.entity.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * DTO representing a request to create a new user in the system.
 */
@Data
@AllArgsConstructor
public class UserCreateRequest {

    /**
     * Full name of the user.
     */
    @NotBlank
    private String fullName;

    /**
     * Email address of the user.
     */
    @Email
    private String email;

    /**
     * Phone number of the user (optional).
     */
    private String phoneNumber;

    /**
     * Password for the user account.
     */
    @NotBlank
    private String password;

    /**
     * Role assigned to the user (e.g., USER, ADMIN).
     */
    @NotNull
    private RoleType role;
}

