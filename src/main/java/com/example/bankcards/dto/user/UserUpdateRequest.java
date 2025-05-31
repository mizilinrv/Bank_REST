package com.example.bankcards.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserUpdateRequest {
    private String fullName;
    private String phoneNumber;
    private String password;
}
