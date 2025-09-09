package com.example.bankcards.security.jwt;

import com.example.bankcards.dto.auth.AuthResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Service for handling JWT token operations such as generation, validation,
 * and extracting information from the token.
 */
@Component
@Slf4j
public class JwtService {

    /** Secret key for signing and verifying JWT tokens,
     *  injected from application properties. */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Generates an authentication response containing
     * a JWT token for the given email.
     *
     * @param email the email of the authenticated user
     * @return AuthResponse containing the generated JWT token
     */
    public AuthResponse generateAuthToken(final String email) {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(generateJwtToken(email));
        return authResponse;
    }

    /**
     * Extracts the email (subject) from the given JWT token.
     *
     * @param token the JWT token
     * @return the email extracted from the token
     */
    public String getEmailFromToken(final String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSingInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    /**
     * Validates the given JWT token for expiration, format, and signature.
     *
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateJwtToken(final String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSingInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Expired JWT token", expEx);
        } catch (UnsupportedJwtException expEx) {
            log.error("Unsupported JWT token", expEx);
        } catch (MalformedJwtException expEx) {
            log.error("Malformed JWT token", expEx);
        } catch (SecurityException expEx) {
            log.error("Security exception while validating JWT", expEx);
        } catch (Exception expEx) {
            log.error("Invalid JWT token", expEx);
        }
        return false;
    }

    /**
     * Generates a JWT token with the given email as the subject.
     * The token is valid for 1 hour from the current time.
     *
     * @param email the email to include in the token
     * @return the generated JWT token
     */
    private String generateJwtToken(final String email) {
        Date date = Date.from(LocalDateTime.now().plusHours(1)
                .atZone(ZoneId.systemDefault())
                .toInstant());
        return Jwts.builder()
                .subject(email)
                .expiration(date)
                .signWith(getSingInKey())
                .compact();
    }

    /**
     * Returns the secret key used for signing and verifying JWT tokens.
     *
     * @return SecretKey used for HMAC SHA signing
     */
    private SecretKey getSingInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

