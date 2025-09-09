package com.example.bankcards.repository;

import com.example.bankcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for {@link User} entities.
 * Provides CRUD operations and custom queries for managing users.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Checks if a user with the given email exists.
     *
     * @param email the email to check
     * @return {@code true} if a user with the email exists,
     * {@code false} otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email of the user
     * @return an {@link Optional} containing the {@link User} if found,
     * or empty if not found
     */
    Optional<User> findByEmail(String email);
}
