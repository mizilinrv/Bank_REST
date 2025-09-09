package com.example.bankcards.security;

import com.example.bankcards.security.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Security configuration class for the application.
 * <p>
 * Configures HTTP security, CORS settings, JWT filter, and password encoding.
 * Uses method security and stateless session management.
 * </p>
 */
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /** JWT filter that validates tokens for incoming requests. */
    private final JwtFilter jwtFilter;

    /**
     * Configures the security filter chain for HTTP requests.
     * <p>
     * Disables HTTP basic authentication and CSRF.
     * Sets CORS configuration.
     * Configures endpoint access rules:
     * <ul>
     *     <li>Public access to authentication and Swagger endpoints</li>
     *     <li>ADMIN role required for
     *     /api/cards/admin/** and /api/users/**</li>
     *     <li>USER role required for /api/cards/user/**</li>
     *     <li>All other requests require authentication</li>
     * </ul>
     * Adds JWT filter before the UsernamePasswordAuthenticationFilter.
     * </p>
     *
     * @param http the {@link HttpSecurity} object
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs while configuring security
     */
    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http)
            throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(
                        corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth.requestMatchers(
                        "/auth/registration", "/auth/login",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/api-docs/**",
                                "/webjars/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(
                                "/api/cards/admin/**",
                                "/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/cards/user/**").hasRole("USER")
                        .anyRequest().authenticated())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Configures CORS settings for the application.
     * <p>
     * Allows requests from http://localhost:3000, supports all HTTP methods,
     * all headers, and allows credentials.
     * </p>
     *
     * @return the {@link CorsConfigurationSource} for CORS
     */
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000"));
        corsConfiguration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }

    /**
     * Provides a password encoder bean using BCrypt with strength of 4.
     *
     * @return the {@link PasswordEncoder} bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        final int bcryptStrength = 4;
        return new BCryptPasswordEncoder(bcryptStrength);
    }
}
