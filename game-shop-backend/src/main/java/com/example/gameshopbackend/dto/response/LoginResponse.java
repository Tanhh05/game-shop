package com.example.gameshopbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class LoginResponse {
    private String token;
    private Long userId;
    private String username;
    private String role;
    private Long balance;
}

