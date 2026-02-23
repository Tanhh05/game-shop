package com.example.gameshopbackend.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BulkImportKeysRequest {
    private Long productId;
    private List<String> keys;
}

