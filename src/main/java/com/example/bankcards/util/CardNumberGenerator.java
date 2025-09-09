package com.example.bankcards.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Component responsible for generating random card numbers.
 *
 * <p>This generator creates a 16-digit card
 * number using a cryptographically secure random number generator.
 */
@Component
public class CardNumberGenerator {

    /** Number of digits in the generated card number. */
    private static final int CARD_NUMBER_LENGTH = 16;

    /** Maximum value for a single digit (0-9). */
    private static final int DIGIT_BOUND = 10;

    /** Random generator used for creating card digits. */
    private final Random random = new SecureRandom();

    /**
     * Generates a random card number.
     *
     * @return a String representing a 16-digit random card number
     */
    public String generate() {
        StringBuilder cardNumber = new StringBuilder();

        for (int i = 0; i < CARD_NUMBER_LENGTH; i++) {
            cardNumber.append(random.nextInt(DIGIT_BOUND));
        }

        return cardNumber.toString();
    }
}
