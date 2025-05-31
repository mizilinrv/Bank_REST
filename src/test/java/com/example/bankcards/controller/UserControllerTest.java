package com.example.bankcards.controller;

import com.example.bankcards.dto.user.UserCreateRequest;
import com.example.bankcards.dto.user.UserResponse;
import com.example.bankcards.dto.user.UserUpdateRequest;
import com.example.bankcards.entity.RoleType;
import com.example.bankcards.security.CustomUserServiceImpl;
import com.example.bankcards.security.jwt.JwtService;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = "ADMIN")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
        @Bean
        public JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }
        @Bean
        public CustomUserServiceImpl customUserService() {
            return Mockito.mock(CustomUserServiceImpl.class);
        }
    }

    @Test
    @DisplayName("Создание пользователя - 201 CREATED")
    void createUser_ShouldReturnCreatedUser() throws Exception {
        UserCreateRequest request = new UserCreateRequest("Ivan Ivanov", "ivan@example.com",
                "+79256982556", "securePassword", RoleType.ROLE_USER);
        UserResponse response = new UserResponse(1L, "Ivan Ivanov", "ivan@example.com",
                "+79256982556", RoleType.ROLE_USER, LocalDateTime.now());

        Mockito.when(userService.createUser(any(UserCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fullName").value("Ivan Ivanov"))
                .andExpect(jsonPath("$.phoneNumber").value("+79256982556"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    @DisplayName("Обновление пользователя - 200 OK")
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest("Ivan Petrov", "+79685896321","SecurePass");
        UserResponse response = new UserResponse(1L, "Ivan Petrov", "ivan@example.com",
                "+79685896321", RoleType.ROLE_USER, LocalDateTime.now());

        Mockito.when(userService.updateUser(eq(1L), any(UserUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Ivan Petrov"))
                .andExpect(jsonPath("$.phoneNumber").value("+79685896321"));
    }

    @Test
    @DisplayName("Получение всех пользователей - 200 OK")
    void getAllUsers_ShouldReturnUserList() throws Exception {
        List<UserResponse> users = List.of(
                new UserResponse(1L, "Ivan Ivanov", "ivan@example.com", "+79586987414",
                        RoleType.ROLE_USER, LocalDateTime.now()),
                new UserResponse(2L, "Ivan Petrov", "petrov@wxample.com", "+79856321478",
                        RoleType.ROLE_USER, LocalDateTime.now())
        );

        Mockito.when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].fullName").value("Ivan Ivanov"));
    }

    @Test
    @DisplayName("Получение пользователя по ID - 200 OK")
    void getUserById_ShouldReturnUser() throws Exception {
        UserResponse response = new UserResponse(1L, "Ivan Ivanov", "ivan@example.com", "+79586987414",
                RoleType.ROLE_USER, LocalDateTime.now());

        Mockito.when(userService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Ivan Ivanov"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    @DisplayName("Удаление пользователя - 204 No Content")
    void deleteUser_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).deleteUser(1L);
    }
}

