package com.example.gameshopbackend.controller;

import com.example.gameshopbackend.dto.response.UserResponse;
import com.example.gameshopbackend.entity.User;
import com.example.gameshopbackend.mapper.UserMapper;
import com.example.gameshopbackend.repository.UserRepository;
import com.example.gameshopbackend.jwt.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Lấy thông tin profile của user
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long userId) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Unauthorized"));
            }

            boolean isAdmin = principal.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

            if (!isAdmin && !principal.getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Bạn không có quyền xem profile của user khác"));
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));
            UserResponse response = userMapper.toResponse(user);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lấy profile thất bại", "detail", ex.getMessage()));
        }
    }

    /**
     * Kiểm tra user tồn tại
     */
    @GetMapping("/exists/{username}")
    public ResponseEntity<?> checkUserExists(@PathVariable String username) {
        try {
            boolean exists = userRepository.existsByUsername(username);
            return ResponseEntity.ok(Map.of("username", username, "exists", exists));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Kiểm tra user thất bại", "detail", ex.getMessage()));
        }
    }
}
