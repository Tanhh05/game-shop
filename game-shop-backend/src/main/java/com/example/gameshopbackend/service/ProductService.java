package com.example.gameshopbackend.service;

import com.example.gameshopbackend.dto.request.ProductRequest;
import com.example.gameshopbackend.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service quản lý Product.
 * Các phương thức trả về DTO, kiểm soát trạng thái và phân trang.
 */
public interface ProductService {

    // Tạo sản phẩm mới
    ProductResponse create(ProductRequest request);

    // Lấy tất cả sản phẩm đang active, có phân trang
    Page<ProductResponse> getAllActive(Pageable pageable);

    // Lấy sản phẩm theo gameId
    List<ProductResponse> getByGame(Long gameId);

    // Lấy sản phẩm theo slug
    ProductResponse getBySlug(String slug);

    // Thay đổi trạng thái active/inactive
    void changeStatus(Long id, Boolean status);
}
