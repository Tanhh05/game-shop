package com.example.gameshopbackend.controller;

import com.example.gameshopbackend.dto.request.LoginRequest;
import com.example.gameshopbackend.dto.request.RegisterRequest;
import com.example.gameshopbackend.jwt.JwtService;
import com.example.gameshopbackend.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {
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
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Token không hợp lệ"));
            }

            String accessToken = authHeader.substring(7);
            Claims claims = jwtService.extractClaims(accessToken);
            Long userId = claims.get("userId", Long.class);
            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            String newToken = jwtService.generateToken(userId, username, role);

            return ResponseEntity.ok(Map.of(
                    "message", "Token được làm mới",
                    "token", newToken
            ));
        } catch (JwtException | IllegalArgumentException ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Token không hợp lệ"));
        }
    }
}
