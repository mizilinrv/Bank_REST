package com.example.bankcards.security.jwt;

import com.example.bankcards.security.CustomUserDetails;
import com.example.bankcards.security.CustomUserServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

/**
 * JWT filter for intercepting HTTP requests and validating JWT tokens.
 * <p>
 * This filter extracts the JWT token from
 * the "Authorization" header, validates it,
 * and sets the corresponding user details in the Spring Security context.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    /** Service for JWT operations such as validation and extracting email. */
    private final JwtService jwtService;

    /** Custom user service to load user details by email. */
    private final CustomUserServiceImpl customUserService;

    /** Length of the "Bearer " prefix in the Authorization header. */
    private static final int BEARER_PREFIX_LENGTH = 7;

    /**
     * Filters incoming requests, validates JWT tokens,
     * and sets authentication in the security context.
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(
            @NonNull final HttpServletRequest request,
                                    @NonNull final HttpServletResponse response,
                                    @NonNull final FilterChain filterChain)
            throws ServletException, IOException {
        String token = getTokenFromRequest(request);
        if (token != null && jwtService.validateJwtToken(token)) {
            setCustomUserDetailsToSecurityContextHolder(token);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Loads user details from the token and
     * sets authentication in the SecurityContextHolder.
     *
     * @param token the validated JWT token
     */
    private void setCustomUserDetailsToSecurityContextHolder(
            final String token
    ) {
        String email = jwtService.getEmailFromToken(token);
        CustomUserDetails customUserDetails =
                customUserService.loadUserByUsername(email);

        Collection<? extends GrantedAuthority> authorities =
                customUserDetails.getAuthorities();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                customUserDetails, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Extracts the JWT token from the Authorization header of the request.
     *
     * @param request the HTTP request
     * @return the JWT token if present and valid, otherwise null
     */
    private String getTokenFromRequest(final HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(BEARER_PREFIX_LENGTH);
        }
        return null;
    }
}
