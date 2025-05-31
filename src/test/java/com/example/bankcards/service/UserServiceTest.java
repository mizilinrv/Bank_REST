package com.example.bankcards.service;

import com.example.bankcards.dto.user.UserCreateRequest;
import com.example.bankcards.dto.user.UserResponse;
import com.example.bankcards.dto.user.UserUpdateRequest;
import com.example.bankcards.entity.RoleType;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ForbiddenOperationException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.UserUpdateException;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;
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
    void getAll_returnsUserResponses() {
        User user = User.builder().id(1L).fullName("John Doe").email("john@example.com").phoneNumber("123456")
                .role(RoleType.ROLE_USER).createdAt(LocalDateTime.now()).build();
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponse> result = userService.getAll();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getFullName());
    }

    @Test
    void getById_whenUserExists_returnsUserResponse() {
        User user = User.builder().id(1L).fullName("John").email("john@example.com")
                .phoneNumber("123").role(RoleType.ROLE_USER).createdAt(LocalDateTime.now()).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse response = userService.getById(1L);

        assertEquals("John", response.getFullName());
    }

    @Test
    void getById_whenUserNotFound_throwsNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(1L));
    }

    @Test
    void deleteUser_whenUserExists_deletesUser() {
        User user = User.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_whenUserNotFound_throwsNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void createUser_successful() {
        UserCreateRequest request = new UserCreateRequest("John", "john@example.com", "123456", "password", RoleType.ROLE_USER);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("hashed");

        User savedUser = User.builder().id(1L).fullName("John").email("john@example.com").phoneNumber("123456")
                .password("hashed").role(RoleType.ROLE_USER).createdAt(LocalDateTime.now()).build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.createUser(request);

        assertEquals("John", response.getFullName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_whenEmailExists_throwsForbidden() {
        UserCreateRequest request = new UserCreateRequest("John", "john@example.com", "123456", "password", RoleType.ROLE_USER);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(ForbiddenOperationException.class, () -> userService.createUser(request));
    }

    @Test
    void createUser_whenIntegrityViolation_throwsForbidden() {
        UserCreateRequest request = new UserCreateRequest("John", "john@example.com", "123456", "password", RoleType.ROLE_USER);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("constraint violation"));

        assertThrows(ForbiddenOperationException.class, () -> userService.createUser(request));
    }

    @Test
    void updateUser_successful() {
        User user = User.builder().id(1L).fullName("Old Name").phoneNumber("000")
                .password("oldPass").createdAt(LocalDateTime.now()).role(RoleType.ROLE_USER).build();
        UserUpdateRequest request = new UserUpdateRequest("New Name", "111", "newPass");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedPass");

        UserResponse response = userService.updateUser(1L, request);

        assertEquals("New Name", response.getFullName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_whenUserNotFound_throwsNotFoundException() {
        UserUpdateRequest request = new UserUpdateRequest("Name", "123", "pass");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserUpdateException.class, () -> userService.updateUser(1L, request));
    }

    @Test
    void updateUser_whenExceptionThrown_throwsUserUpdateException() {
        User user = User.builder().id(1L).fullName("Name").build();
        UserUpdateRequest request = new UserUpdateRequest("Name", "123", "pass");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("pass")).thenThrow(new RuntimeException("encoding error"));

        assertThrows(UserUpdateException.class, () -> userService.updateUser(1L, request));
    }
}

