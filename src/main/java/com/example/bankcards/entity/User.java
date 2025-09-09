package com.example.bankcards.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a user of the banking system.
 * <p>
 * Contains personal information, credentials, role, account creation timestamp,
 * and a list of associated cards.
 * </p>
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /** Unique identifier of the user. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Full name of the user. Cannot be null. */
    @Column(name = "full_name", nullable = false)
    private String fullName;

    /** Email of the user. Must be unique and not null. */
    @Column(nullable = false, unique = true)
    private String email;

    /** Phone number of the user. Optional field. */
    @Column(name = "phone_number")
    private String phoneNumber;

    /** Encrypted password of the user. Cannot be null. */
    @Column(nullable = false)
    private String password;

    /** Role of the user (ADMIN or USER). Cannot be null. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;

    /** Timestamp when the user was created. Not updatable after insertion. */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** List of cards associated with the user. */
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Card> cards;
}

