package com.example.bankcards.controller;

import com.example.bankcards.dto.user.UserCreateRequest;
import com.example.bankcards.dto.user.UserUpdateRequest;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.bankcards.dto.user.UserResponse;

import java.util.List;


/**
 * REST controller for managing users.
 * <p>
 * Provides CRUD operations for user management.
 * Access is restricted to administrators only.
 * </p>
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "User Management API", description = "User management operations (ADMIN only)")
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Creates a new user.
     *
     * @param request the request body containing user creation details
     * @return the created user with status {@code 201 Created}
     */
    @Operation(
            summary = "Create a new user",
            description = "Creates a new user with the provided details and returns the created user.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User successfully created",
                            content = @Content(schema = @Schema(implementation = UserResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid request data")
            }
    )
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody final UserCreateRequest request
    ) {
        UserResponse response = userService.createUser(request);
        log.info("User created: {}", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Updates an existing user.
     *
     * @param id      the ID of the user to update
     * @param request the request body containing updated user details
     * @return the updated user with status {@code 200 OK}
     */
    @Operation(
            summary = "Update user details",
            description = "Updates the user with the given ID and returns the updated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User successfully updated",
                            content = @Content(schema = @Schema(implementation = UserResponse.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "User ID") @PathVariable final Long id,
            @Valid @RequestBody final UserUpdateRequest request
    ) {
        UserResponse response = userService.updateUser(id, request);
        log.info("User updated: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all users.
     *
     * @return the list of all users with status {@code 200 OK}
     */
    @Operation(
            summary = "Get all users",
            description = "Retrieves a list of all users.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of users retrieved successfully",
                            content = @Content(schema = @Schema(implementation = UserResponse.class))
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        List<UserResponse> users = userService.getAll();
        log.info("Found {} users", users.size());
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id the ID of the user
     * @return the found user with status {@code 200 OK}, or {@code 404 Not Found} if not found
     */
    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a user by their ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User found",
                            content = @Content(schema = @Schema(implementation = UserResponse.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(
            @Parameter(description = "User ID") @PathVariable final Long id
    ) {
        UserResponse response = userService.getById(id);
        log.info("User found: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the ID of the user
     * @return {@code 204 No Content} if successfully deleted, or {@code 404 Not Found} if not found
     */
    @Operation(
            summary = "Delete user by ID",
            description = "Deletes a user with the specified ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID") @PathVariable final Long id
    ) {
        userService.deleteUser(id);
        log.info("User with id {} deleted", id);
        return ResponseEntity.noContent().build();
    }
}


