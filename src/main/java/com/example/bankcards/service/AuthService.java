package com.example.bankcards.service;

import com.example.bankcards.dto.auth.AuthResponse;
import com.example.bankcards.dto.auth.LoginRequest;
import com.example.bankcards.dto.auth.RegisterRequest;
import com.example.bankcards.dto.auth.UserPublicResponse;
import com.example.bankcards.entity.RoleType;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.InvalidCredentialsException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.jwt.JwtService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public UserPublicResponse registerUser(RegisterRequest request) {
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(RoleType.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);
        log.info("Пользователь успешно зарегистрирован: {}", user.getEmail());
        return new UserPublicResponse(user.getFullName(), user.getEmail(), user.getPhoneNumber());
    }

    public AuthResponse authenticate(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Пользователь с email {} не найден", request.getEmail());
                    return new NotFoundException("Пользователь с таким email не найден: " + request.getEmail());
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Неверный пароль для пользователя с email: {}", request.getEmail());
            throw new InvalidCredentialsException("Неверный email или пароль");
        }

        return jwtService.generateAuthToken(user.getEmail());
    }
}
