package com.example.bankcards.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents public information about a
 * user that can be exposed via API responses.
 */
@Data
@AllArgsConstructor
public class UserPublicResponse {

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
}
