package com.example.bankcards.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a bank card in the system.
 * <p>
 * Each card is associated with a {@link User}, has an encrypted card number,
 * an expiration date, a status, and a balance.
 * </p>
 */
@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {

    /**
     * The unique identifier of the card.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The encrypted number of the card. Must be unique and not null.
     */
    @Column(name = "encrypted_number", nullable = false, unique = true)
    private String encryptedNumber;

    /**
     * The owner of the card.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The expiration date of the card.
     */
    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    /**
     * The status of the card (e.g., ACTIVE, BLOCKED).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus status;

    /**
     * The balance available on the card.
     */
    @Column(nullable = false)
    private BigDecimal balance;
}
