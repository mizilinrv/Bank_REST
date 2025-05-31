package com.example.bankcards.controller;

import com.example.bankcards.dto.auth.AuthResponse;
import com.example.bankcards.dto.auth.LoginRequest;
import com.example.bankcards.dto.auth.RegisterRequest;
import com.example.bankcards.dto.auth.UserPublicResponse;
import com.example.bankcards.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создает нового пользователя и возвращает публичную информацию о нем",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пользователь успешно зарегистрирован",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserPublicResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Неверный формат запроса")
            }
    )
    @PostMapping("/registration")
    public ResponseEntity<UserPublicResponse> register(
            @Valid @RequestBody final RegisterRequest request
    ) {
        log.info("Пользователь с email {} успешно зарегистрирован", request.getEmail());
        return ResponseEntity.ok(
                authService.registerUser(request)
        );
    }
    @Operation(
            summary = "Аутентификация пользователя",
            description = "Выполняет вход по логину и паролю, возвращает токен и информацию о пользователе",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный вход",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponse.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody final LoginRequest request
    ) {
        log.info("Пользователь с email {} успешно вошел, токен выдан", request.getEmail());
        return ResponseEntity.ok(
                authService.authenticate(request)
        );
    }
}
