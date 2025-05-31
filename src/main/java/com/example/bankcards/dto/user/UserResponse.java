package com.example.bankcards.dto.user;

import com.example.bankcards.entity.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private RoleType role;
    private LocalDateTime createdAt;
}
