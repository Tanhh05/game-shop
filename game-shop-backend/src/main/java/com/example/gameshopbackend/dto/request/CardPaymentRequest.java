package com.example.gameshopbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardPaymentRequest {
    @NotNull(message = "userId is required")
    @Positive(message = "userId must be greater than 0")
    private Long userId;

    @NotNull(message = "amount is required")
    @Positive(message = "amount must be greater than 0")
    private Long amount;

    @NotBlank(message = "cardNumber is required")
    private String cardNumber;        // Luôn encrypt trong thực tế

    @NotBlank(message = "cardHolderName is required")
    private String cardHolderName;

    @NotBlank(message = "expiryDate is required")
    private String expiryDate;        // MM/YY

    @NotBlank(message = "cvv is required")
    private String cvv;               // Luôn encrypt trong thực tế

    @NotBlank(message = "bankCode is required")
    private String bankCode;          // Visa, Mastercard, etc.
}
