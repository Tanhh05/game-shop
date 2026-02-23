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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final GameRepository gameRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse create(ProductRequest request) {
        // validate business fields
        if (request == null) {
            throw new IllegalArgumentException("Dữ liệu sản phẩm không hợp lệ");
        }
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

        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy game với id: " + request.getGameId()));

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
        product.setStatus(request.getStatus() != null ? request.getStatus() : true);

        productRepository.save(product);

        return productMapper.toResponse(product);
    }

    @Override
    public Page<ProductResponse> getAllActive(Pageable pageable) {
        Page<Product> page = productRepository.findByStatusTrue(pageable);
        return page.map(productMapper::toResponse);
    }

    @Override
    public List<ProductResponse> getByGame(Long gameId) {
        return productMapper.toResponseList(
                productRepository.findByGame_IdAndStatusTrue(gameId)
        );
    }

    @Override
    public ProductResponse getBySlug(String slug) {
        Optional<Product> opt = productRepository.findBySlugAndStatusTrue(slug);
        if (opt.isEmpty()) {
            return null; // controller sẽ trả 404
        }
        return productMapper.toResponse(opt.get());
    }

    @Override
    public void changeStatus(Long id, Boolean status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với id: " + id));
        product.setStatus(status);
        productRepository.save(product);
    }
}
