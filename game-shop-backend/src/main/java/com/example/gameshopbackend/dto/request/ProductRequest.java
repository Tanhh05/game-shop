package com.example.gameshopbackend.dto.request;

import com.example.gameshopbackend.util.Platform;
import com.example.gameshopbackend.util.ProductType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductRequest {

    private Long gameId;
    private ProductType type;
    private Platform platform;
    private String title;
    private String shortDescription;
    private String description;
    private String thumbnail;
    private String slug;
    private Boolean status;

    private List<ProductPackageRequest> packages;
}

