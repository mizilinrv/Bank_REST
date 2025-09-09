package com.example.bankcards.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Represents the response returned after successful user authentication.
 * <p>
 * Contains the JWT token issued to the user, which should be included in
 * subsequent requests to access secured endpoints.
 * </p>
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class AuthResponse {

    /**
     * JWT token issued after successful authentication.
     * <p>
     * This token must be sent in the Authorization header of future requests
     * to access protected resources.
     * </p>
     */
    private String token;
}
