package com.example.gameshopbackend.mapper;

import com.example.gameshopbackend.dto.response.ProductPackageResponse;
import com.example.gameshopbackend.dto.response.ProductResponse;
import com.example.gameshopbackend.entity.Product;
import com.example.gameshopbackend.entity.ProductPackage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "game.id", target = "gameId")
    @Mapping(source = "game.name", target = "gameName")
    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponseList(List<Product> products);

    ProductPackageResponse toPackageResponse(ProductPackage pkg);

    List<ProductPackageResponse> toPackageResponseList(List<ProductPackage> packages);
}

