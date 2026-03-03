package com.example.gameshopbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminInventoryKeyResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String licenseKey;
    private String status;
}
