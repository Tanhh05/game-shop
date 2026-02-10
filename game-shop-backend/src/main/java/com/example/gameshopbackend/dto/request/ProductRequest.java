package com.example.gameshopbackend.dto.request;

import com.example.gameshopbackend.util.Platform;
import com.example.gameshopbackend.util.ProductType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {

    private Long gameId;

    private ProductType type;      // KEY / ACCOUNT / TOOL
    private Platform platform;     // ANDROID / IOS / ALL

    private String title;
    private String shortDescription;
    private String description;

    private Long price;
    private String thumbnail;
    private String slug;

    private Boolean status;
}

