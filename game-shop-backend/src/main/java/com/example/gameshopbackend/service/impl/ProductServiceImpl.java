package com.example.gameshopbackend.service.impl;

import com.example.gameshopbackend.dto.request.ProductRequest;
import com.example.gameshopbackend.dto.response.ProductResponse;
import com.example.gameshopbackend.entity.Game;
import com.example.gameshopbackend.entity.Product;
import com.example.gameshopbackend.mapper.ProductMapper;
import com.example.gameshopbackend.repository.GameRepository;
import com.example.gameshopbackend.repository.ProductRepository;
import com.example.gameshopbackend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final GameRepository gameRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse create(ProductRequest request) {

        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new RuntimeException("Game not found"));

        Product product = new Product();
        product.setGame(game);
        product.setType(request.getType());
        product.setPlatform(request.getPlatform());
        product.setTitle(request.getTitle());
        product.setShortDescription(request.getShortDescription());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setThumbnail(request.getThumbnail());
        product.setSlug(request.getSlug());
        product.setStatus(request.getStatus());

        productRepository.save(product);

        return productMapper.toResponse(product);
    }

    @Override
    public List<ProductResponse> getAllActive() {
        return productMapper.toResponseList(productRepository.findByStatusTrue());
    }

    @Override
    public List<ProductResponse> getByGame(Long gameId) {
        return productMapper.toResponseList(
                productRepository.findByGame_IdAndStatusTrue(gameId)
        );
    }

    @Override
    public ProductResponse getBySlug(String slug) {
        Product product = productRepository.findBySlugAndStatusTrue(slug)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        return productMapper.toResponse(product);
    }

    @Override
    public void changeStatus(Long id, Boolean status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        product.setStatus(status);
        productRepository.save(product);
    }
}
