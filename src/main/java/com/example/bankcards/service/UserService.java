package com.example.bankcards.service;

import com.example.bankcards.dto.user.UserCreateRequest;
import com.example.bankcards.dto.user.UserResponse;
import com.example.bankcards.dto.user.UserUpdateRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ForbiddenOperationException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.UserUpdateException;
import com.example.bankcards.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class responsible for managing users.
 * Handles operations like creating, updating, retrieving, and deleting users.
 */
@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    /**
     * Repository interface for accessing
     * and managing User entities in the database.
     */
    private final UserRepository userRepository;

    /**
     * Password encoder used for securely hashing and verifying user passwords.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Retrieves a list of all users.
     *
     * @return list of UserResponse DTOs representing all users
     */
    public List<UserResponse> getAll() {
        log.info("Retrieving the list of all users");
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user
     * @return UserResponse representing the user
     * @throws NotFoundException if the user with the given ID does not exist
     */
    public UserResponse getById(final Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User with ID {} not found", id);
                    return new NotFoundException("User " + id + " not found");
                });
        log.debug("User found: {}", user.getEmail());
        return toResponse(user);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     * @throws NotFoundException if the user with the given ID does not exist
     */
    public void deleteUser(final Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn(
                            "Failed to find user with ID {} for deletion",
                            id
                    );
                    return new NotFoundException(
                            "User with ID " + id + " not found"
                    );
                });

        log.info("User with ID {} successfully deleted", id);
        userRepository.delete(user);
    }

    /**
     * Creates a new user.
     *
     * @param request DTO containing user creation data
     * @return UserResponse representing the newly created user
     * @throws ForbiddenOperationException
     * if the email is already in use
     */
    public UserResponse createUser(final UserCreateRequest request) {
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                log.warn(
                        "Attempt to create a user with an existing email: {}",
                        request.getEmail()
                );
                throw new ForbiddenOperationException(
                        "Email is already in use"
                );
            }

            User user = User.builder()
                    .fullName(request.getFullName())
                    .email(request.getEmail())
                    .phoneNumber(request.getPhoneNumber())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(request.getRole())
                    .createdAt(LocalDateTime.now())
                    .build();

            userRepository.save(user);
            log.info("User created: {}", user.getEmail());

            return toResponse(user);

        } catch (DataIntegrityViolationException e) {
            log.error(
                    "Email uniqueness violation while creating user: {}",
                    request.getEmail(), e
            );
            throw new ForbiddenOperationException(
                    "Email is already in use "
                            + "(database uniqueness constraint violated)"
            );
        }
    }

    /**
     * Updates an existing user by ID.
     *
     * @param id      the ID of the user to update
     * @param request DTO containing user update data
     * @return UserResponse representing the updated user
     * @throws NotFoundException
     * if the user with the given ID does not exist
     * @throws UserUpdateException
     * if any error occurs during update
     */
    public UserResponse updateUser(
            final Long id, final UserUpdateRequest request) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("User with ID {} not found for update", id);
                        return new NotFoundException(
                                "User with ID " + id + " not found!"
                        );
                    });

            if (request.getFullName() != null) {
                log.debug("Updating user full name: {}", request.getFullName());
                user.setFullName(request.getFullName());
            }

            if (request.getPhoneNumber() != null) {
                log.debug(
                        "Updating user phone number: {}",
                        request.getPhoneNumber()
                );
                user.setPhoneNumber(request.getPhoneNumber());
            }

            if (request.getPassword() != null) {
                log.debug("Updating user password");
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            }

            userRepository.save(user);
            log.info("User with ID {} successfully updated", id);
            return toResponse(user);

        } catch (Exception e) {
            log.error(
                    "Error updating user with ID {}: {}",
                    id, e.getMessage(), e
            );
            throw new UserUpdateException(
                    "Error updating user: " + e.getMessage(), e
            );
        }
    }

    /**
     * Converts a User entity to UserResponse DTO.
     *
     * @param user the User entity
     * @return UserResponse representing the user
     */
    private UserResponse toResponse(final User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
