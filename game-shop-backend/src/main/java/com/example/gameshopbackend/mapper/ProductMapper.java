package com.example.gameshopbackend.mapper;

import com.example.gameshopbackend.dto.response.ProductPackageResponse;
import com.example.gameshopbackend.dto.response.ProductResponse;
import com.example.gameshopbackend.entity.Product;
import com.example.gameshopbackend.entity.ProductPackage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        if (product.getGame() != null) {
            response.setGameId(product.getGame().getId());
            response.setGameName(product.getGame().getName());
        }
        response.setType(product.getType());
        response.setPlatform(product.getPlatform());
        response.setTitle(product.getTitle());
        response.setShortDescription(product.getShortDescription());
        response.setDescription(product.getDescription());
        response.setThumbnail(product.getThumbnail());
        response.setSlug(product.getSlug());
        response.setStatus(product.getStatus());
        response.setCreatedAt(product.getCreatedAt());
        response.setPackages(toPackageResponseList(product.getPackages()));
        return response;
    }

    public List<ProductResponse> toResponseList(List<Product> products) {
        if (products == null) {
            return null;
        }
        List<ProductResponse> responses = new ArrayList<>(products.size());
        for (Product product : products) {
            responses.add(toResponse(product));
        }
        return responses;
    }

    public ProductPackageResponse toPackageResponse(ProductPackage pkg) {
        if (pkg == null) {
            return null;
        }
        ProductPackageResponse response = new ProductPackageResponse();
        response.setId(pkg.getId());
        response.setName(pkg.getName());
        response.setPrice(pkg.getPrice());
        response.setDurationValue(pkg.getDurationValue());
        response.setDurationUnit(pkg.getDurationUnit());
        return response;
    }

    public List<ProductPackageResponse> toPackageResponseList(List<ProductPackage> packages) {
        if (packages == null) {
            return null;
        }
        List<ProductPackageResponse> responses = new ArrayList<>(packages.size());
        for (ProductPackage pkg : packages) {
            responses.add(toPackageResponse(pkg));
        }
        return responses;
    }
}
