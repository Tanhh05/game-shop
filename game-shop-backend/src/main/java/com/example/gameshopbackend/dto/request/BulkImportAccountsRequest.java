package com.example.gameshopbackend.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BulkImportAccountsRequest {
    private Long productId;

    @Getter
    @Setter
    public static class Account {
        private String username;
        private String password;
    }

    private List<Account> accounts;
}

