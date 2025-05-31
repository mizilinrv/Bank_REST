package com.example.bankcards.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserPublicResponse {
    private String fullName;
    private String email;
    private String phoneNumber;
}
