package com.example.gameshopbackend.mapper;

import com.example.gameshopbackend.dto.request.GameRequest;
import com.example.gameshopbackend.dto.response.GameResponse;
import com.example.gameshopbackend.entity.Game;
import org.springframework.stereotype.Component;

@Component
public class GameMapper {

    public GameResponse toResponse(Game game) {
        if (game == null) {
            return null;
        }
        return new GameResponse(
                game.getId(),
                game.getName(),
                game.getSlug(),
                game.getThumbnail(),
                game.getDescription(),
                game.getStatus(),
                game.getCreatedAt()
        );
    }

    public Game toEntity(GameRequest request) {
        if (request == null) {
            return null;
        }
        Game game = new Game();
        game.setName(request.getName());
        game.setSlug(request.getSlug());
        game.setThumbnail(request.getThumbnail());
        game.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            game.setStatus(request.getStatus());
        }
        return game;
    }
}
