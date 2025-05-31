package com.example.bankcards.controller;

import com.example.bankcards.dto.user.UserCreateRequest;
import com.example.bankcards.dto.user.UserUpdateRequest;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.bankcards.dto.user.UserResponse;

import java.util.List;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "User Management API", description = "Операции управления пользователями (только для ADMIN)")
@Slf4j
public class UserController {

    private final UserService userService;

    @Operation(summary = "Создание нового пользователя",
    responses = {
            @ApiResponse(responseCode = "201", description = "Пользователь создан",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации запроса")
    })
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody final UserCreateRequest request
    ) {
        UserResponse response = userService.createUser(request);
        log.info("Пользователь создан: {}", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Обновление данных пользователя",
    responses = {
            @ApiResponse(responseCode = "200", description = "Пользователь обновлён",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "ID пользователя") @PathVariable final Long id,
            @Valid @RequestBody final UserUpdateRequest request
    ) {
        UserResponse response = userService.updateUser(id, request);
        log.info("Пользователь обновлён: {}", response);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получение списка всех пользователей")
    @ApiResponse(responseCode = "200", description = "Список пользователей",
            content = @Content(schema = @Schema(implementation = UserResponse.class)))
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        List<UserResponse> users = userService.getAll();
        log.info("Найдено пользователей: {}", users.size());
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Получение пользователя по ID",
    responses = {
            @ApiResponse(responseCode = "200", description = "Найденный пользователь",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(
            @Parameter(description = "ID пользователя") @PathVariable final Long id
    ) {
        UserResponse response = userService.getById(id);
        log.info("Пользователь найден: {}", response);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Удаление пользователя по ID",
    responses = {
            @ApiResponse(responseCode = "204", description = "Пользователь удалён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID пользователя") @PathVariable final Long id
    ) {
        userService.deleteUser(id);
        log.info("Пользователь с id {} удалён", id);
        return ResponseEntity.noContent().build();
    }
}

