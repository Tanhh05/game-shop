package com.example.gameshopbackend.service;

import com.example.gameshopbackend.dto.request.ProductRequest;
import com.example.gameshopbackend.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    public abstract ProductResponse create(ProductRequest request);

    public abstract List<ProductResponse> getAllActive();

    public abstract List<ProductResponse> getByGame(Long gameId);

    public abstract ProductResponse getBySlug(String slug);

    public abstract void changeStatus(Long id, Boolean status);
}
