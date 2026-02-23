package com.example.gameshopbackend.service.impl;

import com.example.gameshopbackend.dto.request.GameRequest;
import com.example.gameshopbackend.dto.response.GameResponse;
import com.example.gameshopbackend.entity.Game;
import com.example.gameshopbackend.mapper.GameMapper;
import com.example.gameshopbackend.repository.GameRepository;
import com.example.gameshopbackend.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final GameMapper gameMapper = GameMapper.INSTANCE;

    @Override
    public Page<GameResponse> getAll(Pageable pageable) {
        return gameRepository.findAll(pageable)
                .map(gameMapper::toResponse);
    }

    @Override
    public Page<GameResponse> getAllActive(Pageable pageable) {
        return gameRepository.findByStatus(true, pageable)
                .map(gameMapper::toResponse);
    }

    @Override
    public GameResponse getById(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Game không tồn tại với ID: " + id));
        return gameMapper.toResponse(game);
    }

    @Override
    public GameResponse getBySlug(String slug) {
        Game game = gameRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Game không tồn tại với slug: " + slug));
        return gameMapper.toResponse(game);
    }

    @Override
    @Transactional
    public GameResponse create(GameRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Tên game là bắt buộc");
        }
        if (request.getSlug() == null || request.getSlug().isBlank()) {
            throw new IllegalArgumentException("Slug là bắt buộc");
        }

        // Kiểm tra slug đã tồn tại
        if (gameRepository.findBySlug(request.getSlug()).isPresent()) {
            throw new IllegalArgumentException("Slug đã tồn tại");
        }

        Game game = gameMapper.toEntity(request);
        Game saved = gameRepository.save(game);
        return gameMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public GameResponse update(Long id, GameRequest request) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Game không tồn tại với ID: " + id));

        if (request.getName() != null && !request.getName().isBlank()) {
            game.setName(request.getName());
        }
        if (request.getSlug() != null && !request.getSlug().isBlank()) {
            game.setSlug(request.getSlug());
        }
        if (request.getThumbnail() != null) {
            game.setThumbnail(request.getThumbnail());
        }
        if (request.getDescription() != null) {
            game.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            game.setStatus(request.getStatus());
        }

        Game updated = gameRepository.save(game);
        return gameMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void changeStatus(Long id, Boolean status) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Game không tồn tại với ID: " + id));
        game.setStatus(status);
        gameRepository.save(game);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!gameRepository.existsById(id)) {
            throw new IllegalArgumentException("Game không tồn tại với ID: " + id);
        }
        gameRepository.deleteById(id);
    }
}

