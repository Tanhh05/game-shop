package com.example.gameshopbackend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemResponse {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Long price;

    private String key;
    private String username;
    private String password;
}