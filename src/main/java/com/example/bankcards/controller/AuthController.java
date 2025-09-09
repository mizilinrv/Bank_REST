package com.example.bankcards.controller;

import com.example.bankcards.dto.auth.AuthResponse;
import com.example.bankcards.dto.auth.LoginRequest;
import com.example.bankcards.dto.auth.RegisterRequest;
import com.example.bankcards.dto.auth.UserPublicResponse;
import com.example.bankcards.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authentication and registration operations.
 *
 * <p>
 * This controller provides endpoints for user registration and login.
 * It delegates the business logic to {@link AuthService}.
 * </p>
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    /**
     * Service layer for handling authentication and registration logic.
     * <p>
     * Provides methods to register new users and authenticate existing ones.
     * The controller delegates all business logic related to authentication
     * to this service.
     * </p>
     */
    private final AuthService authService;

    /**
     * Registers a new user and returns their public information.
     *
     * @param request the registration request containing user details
     * @return a response entity containing the public user information
     */
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user and "
                    + "returns their public information",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User successfully registered",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation =
                                                    UserPublicResponse.class
                                    ))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request format")
            }
    )
    @PostMapping("/registration")
    public ResponseEntity<UserPublicResponse> register(
            @Valid @RequestBody final RegisterRequest request
    ) {
        log.info(
                "User with email {} successfully registered",
                request.getEmail());
        return ResponseEntity.ok(
                authService.registerUser(request)
        );
    }

    /**
     * Authenticates a user and returns a JWT token with user information.
     *
     * @param request the login request containing credentials
     * @return a response entity containing authentication details
     */
    @Operation(
            summary = "Authenticate user",
            description = "Performs login using"
                    + " credentials and returns a token with user information",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully authenticated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = AuthResponse.class
                                    ))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid credentials"
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody final LoginRequest request
    ) {
        log.info(
                "User with email {} successfully logged in, token issued",
                request.getEmail()
        );
        return ResponseEntity.ok(
                authService.authenticate(request)
        );
    }
}
