package com.example.gameshopbackend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BulkImportAccountsRequest {
    @NotNull(message = "productId is required")
    @Positive(message = "productId must be greater than 0")
    private Long productId;

    @Getter
    @Setter
    public static class Account {
        @NotBlank(message = "username is required")
        private String username;

        @NotBlank(message = "password is required")
        private String password;
    }

    @NotEmpty(message = "accounts must not be empty")
    @Valid
    private List<Account> accounts;
}
