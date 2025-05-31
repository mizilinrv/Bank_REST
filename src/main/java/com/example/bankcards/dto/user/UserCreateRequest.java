package com.example.bankcards.dto.user;

import com.example.bankcards.entity.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class UserCreateRequest {
    @NotBlank
    private String fullName;

    @Email
    private String email;

    private String phoneNumber;

    @NotBlank
    private String password;

    @NotNull
    private RoleType role;
}
