package com.example.gameshopbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ATMPaymentRequest {
    @NotNull(message = "userId is required")
    @Positive(message = "userId must be greater than 0")
    private Long userId;

    @NotNull(message = "amount is required")
    @Positive(message = "amount must be greater than 0")
    private Long amount;

    @NotBlank(message = "bankCode is required")
    private String bankCode;         // Tên ngân hàng

    @NotBlank(message = "bankAccountNumber is required")
    private String bankAccountNumber;

    @NotBlank(message = "bankAccountName is required")
    private String bankAccountName;

    private String description;      // Nội dung chuyển khoản
}
