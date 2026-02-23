package com.example.gameshopbackend.mapper;

import com.example.gameshopbackend.dto.request.UserRequest;
import com.example.gameshopbackend.dto.response.UserResponse;
import com.example.gameshopbackend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserResponse toResponse(User user);

    User toEntity(UserRequest request);
}

