package com.example.bankcards.service;

import com.example.bankcards.dto.auth.AuthResponse;
import com.example.bankcards.dto.auth.LoginRequest;
import com.example.bankcards.dto.auth.RegisterRequest;
import com.example.bankcards.dto.auth.UserPublicResponse;
import com.example.bankcards.entity.RoleType;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.InvalidCredentialsException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.jwt.JwtService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service responsible for authentication and registration operations.
 * <p>
 * This service handles user registration, authentication, password encoding,
 * and JWT token generation.
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class AuthService {

    /** Repository for performing CRUD operations on users. */
    private final UserRepository userRepository;

    /** Password encoder for hashing and validating passwords. */
    private final PasswordEncoder passwordEncoder;

    /** Service for generating and validating JWT tokens. */
    private final JwtService jwtService;

    /**
     * Registers a new user in the system.
     *
     * @param request the registration request containing user details
     * @return a public response with the registered user's information
     */
    public UserPublicResponse registerUser(final RegisterRequest request) {
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(RoleType.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);
        log.info("User successfully registered: {}", user.getEmail());
        return new UserPublicResponse(
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber()
        );
    }

    /**
     * Authenticates a user using their email and password.
     *
     * @param request the login request containing email and password
     * @return an authentication response containing the JWT token
     * @throws NotFoundException if the user with the given email does not exist
     * @throws InvalidCredentialsException if the password is incorrect
     */
    public AuthResponse authenticate(final LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn(
                            "User with email {} not found",
                            request.getEmail()
                    );
                    return new NotFoundException(
                            "User with email not found: " + request.getEmail()
                    );
                });

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())
        ) {
            log.warn(
                    "Invalid password for user with email: {}",
                    request.getEmail()
            );
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return jwtService.generateAuthToken(user.getEmail());
    }
}
