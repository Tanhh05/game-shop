package com.example.gameshopbackend.dto.response;

import com.example.gameshopbackend.util.Platform;
import com.example.gameshopbackend.util.ProductType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ProductResponse {

    private Long id;
    private Long gameId;
    private String gameName;
    private ProductType type;
    private Platform platform;
    private String title;
    private String shortDescription;
    private String description;
    private String thumbnail;
    private String slug;
    private Boolean status;
    private LocalDateTime createdAt;

    private List<ProductPackageResponse> packages;
}
