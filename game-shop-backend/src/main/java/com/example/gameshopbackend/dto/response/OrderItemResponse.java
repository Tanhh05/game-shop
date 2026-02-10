package com.example.gameshopbackend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemResponse {
    private Long productId;
    private String productName;
    private Long price;
    private Integer quantity;
}
