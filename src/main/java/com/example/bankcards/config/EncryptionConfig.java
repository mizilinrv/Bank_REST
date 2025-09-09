package com.example.bankcards.config;

import com.example.bankcards.util.CardEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class for encryption setup.
 * <p>
 * This class loads a secret key from the application configuration
 * and provides a {@link CardEncryptor} bean, which is responsible
 * for encrypting and decrypting sensitive card data.
 * </p>
 *
 * Example in {@code application.properties}:
 * <pre>
 * encryption.secret-key=MySuperSecretKey123
 * </pre>
 */
@Configuration
public class EncryptionConfig {

    /**
     * Secret key used for initializing the {@link CardEncryptor}.
     * <p>
     * The value is injected from the application configuration
     * (e.g., {@code application.properties} or {@code application.yml}).
     * </p>
     */
    @Value("${encryption.secret-key}")
    private String secretKey;

    /**
     * Defines the {@link CardEncryptor} bean in the Spring context.
     *
     * @return a {@link CardEncryptor} instance initialized with the secret key
     */
    @Bean
    public CardEncryptor cardEncryptor() {
        return new CardEncryptor(secretKey);
    }
}
