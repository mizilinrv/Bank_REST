package com.example.bankcards.service;

import com.example.bankcards.dto.user.UserCreateRequest;
import com.example.bankcards.dto.user.UserResponse;
import com.example.bankcards.dto.user.UserUpdateRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ForbiddenOperationException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.UserUpdateException;
import com.example.bankcards.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getAll() {
        log.info("Получение списка всех пользователей");
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Пользователь с ID {} не найден", id);
                    return new NotFoundException("Пользователь " + id + " не найден");
                });
        log.debug("Пользователь найден: {}", user.getEmail());
        return toResponse(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Не удалось найти пользователя с ID {} для удаления", id);
                    return new NotFoundException("Пользователь с ID " + id + " не найден");
                });

        log.info("Пользователь с ID {} успешно удалён", id);
        userRepository.delete(user);

    }
    public UserResponse createUser(UserCreateRequest request) {
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                log.warn("Попытка создать пользователя с уже существующим email: {}", request.getEmail());
                throw new ForbiddenOperationException("Email уже используется");
            }

            User user = User.builder()
                    .fullName(request.getFullName())
                    .email(request.getEmail())
                    .phoneNumber(request.getPhoneNumber())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(request.getRole())
                    .createdAt(LocalDateTime.now())
                    .build();

            userRepository.save(user);
            log.info("Пользователь создан: {}", user.getEmail());

            return toResponse(user);

        } catch (DataIntegrityViolationException e) {
            log.error("Нарушение уникальности email при создании пользователя: {}", request.getEmail(), e);
            throw new ForbiddenOperationException("Email уже используется (уникальность нарушена на уровне базы данных)");
        }
    }
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Пользователь с ID {} не найден для обновления", id);
                        return new NotFoundException("Пользователь с ID " + id + " не найден!");
                    });

            if (request.getFullName() != null) {
                log.debug("Обновление имени пользователя: {}", request.getFullName());
                user.setFullName(request.getFullName());
            }

            if (request.getPhoneNumber() != null) {
                log.debug("Обновление телефона пользователя: {}", request.getPhoneNumber());
                user.setPhoneNumber(request.getPhoneNumber());
            }

            if (request.getPassword() != null) {
                log.debug("Обновление пароля пользователя");
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            }

            userRepository.save(user);
            log.info("Пользователь с ID {} успешно обновлён", id);
            return toResponse(user);

        } catch (Exception e) {
            log.error("Ошибка при обновлении пользователя с ID {}: {}", id, e.getMessage(), e);
            throw new UserUpdateException("Ошибка при обновлении пользователя: " + e.getMessage(), e);
        }
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(),
                user.getPhoneNumber(), user.getRole(), user.getCreatedAt());
    }
}
