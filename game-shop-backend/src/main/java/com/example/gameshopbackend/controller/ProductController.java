package com.example.gameshopbackend.controller;

import com.example.gameshopbackend.dto.request.ProductRequest;
import com.example.gameshopbackend.dto.response.ProductResponse;
import com.example.gameshopbackend.service.MinioService;
import com.example.gameshopbackend.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final MinioService minioService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> create(
            @RequestPart("data") String data,
            @RequestPart("file") MultipartFile file
    ) throws Exception {

        ProductRequest request = objectMapper.readValue(data, ProductRequest.class);

        String imageUrl = minioService.upload(file);
        request.setThumbnail(imageUrl);

        return ResponseEntity.ok(productService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(productService.getAllActive());
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<ProductResponse>> getByGame(@PathVariable Long gameId) {
        return ResponseEntity.ok(productService.getByGame(gameId));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProductResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productService.getBySlug(slug));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable Long id,
            @RequestParam Boolean status
    ) {
        productService.changeStatus(id, status);
        return ResponseEntity.ok().build();
    }
}

