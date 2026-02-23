package com.example.gameshopbackend.service;

import com.example.gameshopbackend.dto.request.GameRequest;
import com.example.gameshopbackend.dto.response.GameResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GameService {
    /**
     * Lấy tất cả game
     */
    Page<GameResponse> getAll(Pageable pageable);

    /**
     * Lấy tất cả game đang active
     */
    Page<GameResponse> getAllActive(Pageable pageable);

    /**
     * Lấy chi tiết game theo ID
     */
    GameResponse getById(Long id);

    /**
     * Lấy game theo slug
     */
    GameResponse getBySlug(String slug);

    /**
     * Tạo game mới
     */
    GameResponse create(GameRequest request);

    /**
     * Cập nhật game
     */
    GameResponse update(Long id, GameRequest request);

    /**
     * Thay đổi trạng thái game
     */
    void changeStatus(Long id, Boolean status);

    /**
     * Xóa game
     */
    void delete(Long id);
}

