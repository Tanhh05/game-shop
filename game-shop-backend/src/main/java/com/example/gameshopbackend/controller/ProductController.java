package com.example.gameshopbackend.controller;

import com.example.gameshopbackend.dto.request.ProductRequest;
import com.example.gameshopbackend.dto.response.ProductResponse;
import com.example.gameshopbackend.service.MinioService;
import com.example.gameshopbackend.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final MinioService minioService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @RequestPart("data") String data,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        // validate input presence
        if (data == null || data.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Dữ liệu sản phẩm không được để trống"));
        }

        try {
            ProductRequest request = objectMapper.readValue(data, ProductRequest.class);

            // validate business fields
            if (request.getGameId() == null) {
                throw new IllegalArgumentException("gameId là bắt buộc");
            }
            if (request.getTitle() == null || request.getTitle().isBlank()) {
                throw new IllegalArgumentException("Title là bắt buộc");
            }
            if (request.getSlug() == null || request.getSlug().isBlank()) {
                throw new IllegalArgumentException("Slug là bắt buộc");
            }
            if (request.getPrice() == null || request.getPrice() < 0) {
                throw new IllegalArgumentException("Price phải là số >= 0");
            }

            if (file != null && !file.isEmpty()) {
                String imageUrl = minioService.upload(file);
                request.setThumbnail(imageUrl);
            }

            ProductResponse created = productService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Tạo sản phẩm thất bại", "detail", ex.getMessage()));
        }
    }

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

            Page<ProductResponse> result = productService.getAllActive(pageable);
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lấy danh sách sản phẩm thất bại", "detail", ex.getMessage()));
        }
    }


    @GetMapping("/game/{gameId}")
    public ResponseEntity<?> getByGame(@PathVariable Long gameId) {
        try {
            List<ProductResponse> list = productService.getByGame(gameId);
            return ResponseEntity.ok(list);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lấy sản phẩm theo game thất bại", "detail", ex.getMessage()));
        }
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getBySlug(@PathVariable String slug) {
        try {
            ProductResponse resp = productService.getBySlug(slug);
            if (resp == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy sản phẩm với slug: " + slug));
            }
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lấy sản phẩm thất bại", "detail", ex.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(
            @PathVariable Long id,
            @RequestParam Boolean status
    ) {
        try {
            if (id == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Id sản phẩm không hợp lệ"));
            }
            productService.changeStatus(id, status);
            return ResponseEntity.ok(Map.of("message", "Cập nhật trạng thái thành công"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Cập nhật trạng thái thất bại", "detail", ex.getMessage()));
        }
    }
}
