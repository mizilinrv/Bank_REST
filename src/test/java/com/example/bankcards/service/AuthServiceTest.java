package com.example.bankcards.service;

import com.example.bankcards.dto.auth.AuthResponse;
import com.example.bankcards.dto.auth.LoginRequest;
import com.example.bankcards.dto.auth.RegisterRequest;
import com.example.bankcards.dto.auth.UserPublicResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.InvalidCredentialsException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.jwt.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("Регистрация пользователя - успех")
    void registerUser_ShouldReturnUserPublicResponse() {

        RegisterRequest request = new RegisterRequest("Ivan Petrov", "ivan@example.com", "1234567890", "secret");

        when(passwordEncoder.encode("secret")).thenReturn("hashedPassword");


        UserPublicResponse response = authService.registerUser(request);


        assertEquals(request.getFullName(), response.getFullName());
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(request.getPhoneNumber(), response.getPhoneNumber());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Аутентификация - успех")
    void authenticate_ShouldReturnAuthToken() {

        LoginRequest request = new LoginRequest("ivan@example.com", "secret");
        User user = User.builder()
                .email("ivan@example.com")
                .password("hashedPassword")
                .build();

        when(userRepository.findByEmail("ivan@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "hashedPassword")).thenReturn(true);
        when(jwtService.generateAuthToken("ivan@example.com")).thenReturn(new AuthResponse("token123"));


        AuthResponse response = authService.authenticate(request);


        assertNotNull(response);
        assertEquals("token123", response.getToken());
    }

    @Test
    @DisplayName("Аутентификация - пользователь не найден")
    void authenticate_ShouldThrowNotFoundException_WhenUserNotFound() {

        LoginRequest request = new LoginRequest("unknown@example.com", "secret");

        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> authService.authenticate(request));
        assertTrue(ex.getMessage().contains("Пользователь с таким email не найден"));
    }

    @Test
    @DisplayName("Аутентификация - неверный пароль")
    void authenticate_ShouldThrowInvalidCredentialsException_WhenPasswordIncorrect() {

        LoginRequest request = new LoginRequest("ivan@example.com", "wrongPassword");
        User user = User.builder()
                .email("ivan@example.com")
                .password("hashedPassword")
                .build();

        when(userRepository.findByEmail("ivan@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        
        InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class, () -> authService.authenticate(request));
        assertEquals("Неверный email или пароль", ex.getMessage());
    }
}

