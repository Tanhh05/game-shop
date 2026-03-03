package com.example.gameshopbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BulkImportKeysRequest {
    @NotNull(message = "productId is required")
    @Positive(message = "productId must be greater than 0")
    private Long productId;

    @NotEmpty(message = "keys must not be empty")
    private List<@NotBlank(message = "key must not be blank") String> keys;
}
