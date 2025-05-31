package com.example.bankcards.config;

import com.example.bankcards.util.CardEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EncryptionConfig {

    @Value("${encryption.secret-key}")
    private String secretKey;

    @Bean
    public CardEncryptor cardEncryptor() {
        return new CardEncryptor(secretKey);
    }
}
