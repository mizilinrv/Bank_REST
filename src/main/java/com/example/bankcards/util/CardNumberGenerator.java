package com.example.bankcards.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

@Component
public class CardNumberGenerator {

    public String generate() {
        StringBuilder cardNumber = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }
        return cardNumber.toString();
    }
}
