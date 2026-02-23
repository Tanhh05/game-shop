package com.example.gameshopbackend.mapper;

import com.example.gameshopbackend.dto.request.GameRequest;
import com.example.gameshopbackend.dto.response.GameResponse;
import com.example.gameshopbackend.entity.Game;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GameMapper {
    GameMapper INSTANCE = Mappers.getMapper(GameMapper.class);

    GameResponse toResponse(Game game);

    Game toEntity(GameRequest request);
}

