package com.example.gameshopbackend.controller;

import com.example.gameshopbackend.dto.request.LoginRequest;
import com.example.gameshopbackend.dto.request.RegisterRequest;
import com.example.gameshopbackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Đăng xuất (phía backend chỉ cần thông báo, phía client xóa token)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Đăng xuất thành công"));
    }

    /**
     * Làm mới token (refresh token)
     * Trong thực tế cần implement refresh token storage
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            @RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Token không hợp lệ"));
            }
            // Ở đây nên validate token và cấp token mới
            return ResponseEntity.ok(Map.of("message", "Token được làm mới", "token", token));
        } catch (Exception ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Token không hợp lệ"));
        }
    }
}
