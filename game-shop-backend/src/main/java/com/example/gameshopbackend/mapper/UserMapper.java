package com.example.gameshopbackend.mapper;

import com.example.gameshopbackend.dto.request.UserRequest;
import com.example.gameshopbackend.dto.response.UserResponse;
import com.example.gameshopbackend.entity.User;
import com.example.gameshopbackend.util.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        String role = user.getRole() != null ? user.getRole().name() : null;
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                role,
                user.getStatus(),
                user.getCreatedAt()
        );
    }

    public User toEntity(UserRequest request) {
        if (request == null) {
            return null;
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        if (request.getRole() != null) {
            user.setRole(Role.valueOf(request.getRole()));
        }
        user.setStatus(request.getStatus());
        return user;
    }
}
