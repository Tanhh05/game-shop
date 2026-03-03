package com.example.gameshopbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminInventoryAccountResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String username;
    private String password;
    private String status;
}
