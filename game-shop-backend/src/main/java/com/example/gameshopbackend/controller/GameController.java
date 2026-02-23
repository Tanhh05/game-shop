package com.example.gameshopbackend.controller;

import com.example.gameshopbackend.dto.request.GameRequest;
import com.example.gameshopbackend.dto.response.GameResponse;
import com.example.gameshopbackend.service.GameService;
import com.example.gameshopbackend.service.MinioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final MinioService minioService;
    private final ObjectMapper objectMapper;

    /**
     * Lấy danh sách game (có phân trang)
     */
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        try {
            Sort sort = direction.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<GameResponse> result = gameService.getAllActive(pageable);
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lấy danh sách game thất bại", "detail", ex.getMessage()));
        }
    }

    /**
     * Lấy chi tiết game theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            GameResponse game = gameService.getById(id);
            return ResponseEntity.ok(game);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lấy game thất bại", "detail", ex.getMessage()));
        }
    }

    /**
     * Lấy game theo slug
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getBySlug(@PathVariable String slug) {
        try {
            GameResponse game = gameService.getBySlug(slug);
            return ResponseEntity.ok(game);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lấy game thất bại", "detail", ex.getMessage()));
        }
    }

    /**
     * Tạo game mới (Admin only)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @RequestPart("data") String data,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        try {
            GameRequest request = objectMapper.readValue(data, GameRequest.class);

            if (request.getName() == null || request.getName().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Tên game là bắt buộc"));
            }
            if (request.getSlug() == null || request.getSlug().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Slug là bắt buộc"));
            }

            if (file != null && !file.isEmpty()) {
                String imageUrl = minioService.upload(file);
                request.setThumbnail(imageUrl);
            }

            GameResponse created = gameService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Tạo game thất bại", "detail", ex.getMessage()));
        }
    }

    /**
     * Cập nhật game (Admin only)
     */
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestPart("data") String data,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        try {
            GameRequest request = objectMapper.readValue(data, GameRequest.class);

            if (file != null && !file.isEmpty()) {
                String imageUrl = minioService.upload(file);
                request.setThumbnail(imageUrl);
            }

            GameResponse updated = gameService.update(id, request);
            return ResponseEntity.ok(updated);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Cập nhật game thất bại", "detail", ex.getMessage()));
        }
    }

    /**
     * Thay đổi trạng thái game (Admin only)
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(
            @PathVariable Long id,
            @RequestParam Boolean status
    ) {
        try {
            gameService.changeStatus(id, status);
            return ResponseEntity.ok(Map.of("message", "Cập nhật trạng thái game thành công"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Cập nhật trạng thái thất bại", "detail", ex.getMessage()));
        }
    }

    /**
     * Xóa game (Admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            gameService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Xóa game thành công"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Xóa game thất bại", "detail", ex.getMessage()));
        }
    }
}

