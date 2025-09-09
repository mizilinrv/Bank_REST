package com.example.bankcards.util;


import com.example.bankcards.exception.EncryptionException;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
/**
 * Utility class for encrypting and
 * decrypting card numbers using AES-GCM algorithm.
 * Provides secure encryption and decryption methods for sensitive card data.
 */
public class CardEncryptor {

    /** AES encryption algorithm with GCM mode and no padding. */
    private static final String ALGORITHM = "AES/GCM/NoPadding";

    /** Length of the Initialization Vector (IV) in bytes for GCM. */
    private static final int GCM_IV_LENGTH = 12;

    /** Length of authentication tag in bits for GCM. */
    private static final int GCM_TAG_LENGTH = 128;

    /** Secret key used for encryption and decryption. */
    private final SecretKeySpec key;

    /** Secure random generator for IV creation. */
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Constructs a CardEncryptor with the specified secret key.
     *
     * @param secretKey the secret key for AES encryption
     */
    public CardEncryptor(final String secretKey) {
        this.key = new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8), "AES");
    }

    /**
     * Encrypts a card number using AES-GCM.
     *
     * @param cardNumber the plaintext card number
     * @return Base64-encoded encrypted card number
     * @throws EncryptionException if an error occurs during encryption
     */
    public String encrypt(final String cardNumber) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
            byte[] encrypted = cipher.doFinal(
                    cardNumber.getBytes(StandardCharsets.UTF_8));

            byte[] combined = ByteBuffer.allocate(iv.length + encrypted.length)
                    .put(iv)
                    .put(encrypted)
                    .array();

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new EncryptionException("Card encryption failed", e);
        }
    }

    /**
     * Decrypts a previously encrypted card number.
     *
     * @param encryptedCardNumber Base64-encoded encrypted card number
     * @return the decrypted plaintext card number
     * @throws EncryptionException if an error occurs during decryption
     */
    public String decrypt(final String encryptedCardNumber) {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedCardNumber);
            byte[] iv = Arrays.copyOfRange(combined, 0, GCM_IV_LENGTH);
            byte[] encrypted = Arrays.copyOfRange(
                    combined, GCM_IV_LENGTH,
                    combined.length
            );

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new EncryptionException("Card decryption failed", e);
        }
    }
}
