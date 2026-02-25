package com.example.gameshopbackend.service.impl;

import com.example.gameshopbackend.dto.request.ProductPackageRequest;
import com.example.gameshopbackend.dto.request.ProductRequest;
import com.example.gameshopbackend.dto.response.ProductResponse;
import com.example.gameshopbackend.entity.Game;
import com.example.gameshopbackend.entity.Product;
import com.example.gameshopbackend.entity.ProductPackage;
import com.example.gameshopbackend.mapper.ProductMapper;
import com.example.gameshopbackend.repository.GameRepository;
import com.example.gameshopbackend.repository.ProductPackageRepository;
import com.example.gameshopbackend.repository.ProductRepository;
import com.example.gameshopbackend.service.ProductService;
import com.example.gameshopbackend.util.ProductType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final GameRepository gameRepository;
    private final ProductMapper productMapper;
    private final ProductPackageRepository productPackageRepository;

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {

        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        Product product = new Product();
        product.setGame(game);
        product.setType(request.getType());
        product.setPlatform(request.getPlatform());
        product.setTitle(request.getTitle());
        product.setShortDescription(request.getShortDescription());
        product.setDescription(request.getDescription());
        product.setThumbnail(request.getThumbnail());
        product.setSlug(request.getSlug());
        product.setStatus(request.getStatus() != null ? request.getStatus() : true);

        // 🔥 Nếu là KEY thì add package vào product trước khi save
        if (request.getType() == ProductType.KEY) {

            if (request.getPackages() == null || request.getPackages().isEmpty()) {
                throw new IllegalArgumentException("KEY phải có ít nhất 1 package");
            }

            for (ProductPackageRequest pkgReq : request.getPackages()) {

                ProductPackage pkg = new ProductPackage();
                pkg.setProduct(product);   // set quan hệ ngược
                pkg.setName(pkgReq.getName());
                pkg.setPrice(pkgReq.getPrice());
                pkg.setDurationValue(pkgReq.getDurationValue());
                pkg.setDurationUnit(pkgReq.getDurationUnit());

                product.getPackages().add(pkg); // 🔥 QUAN TRỌNG
            }
        }

        if (request.getType() == ProductType.ACCOUNT && request.getPackages() != null) {
            throw new IllegalArgumentException("ACCOUNT không được có package");
        }

        // 🔥 Chỉ cần save product (cascade sẽ save package)
        Product savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
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
