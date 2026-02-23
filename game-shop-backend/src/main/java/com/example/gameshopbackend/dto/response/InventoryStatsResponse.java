package com.example.gameshopbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InventoryStatsResponse {
    private Long productId;
    private String productName;
    private Long availableKeys;
    private Long soldKeys;
    private Long availableAccounts;
    private Long soldAccounts;
    private Long totalInventory;
}

