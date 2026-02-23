package com.example.gameshopbackend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardPaymentRequest {
    private Long userId;
    private Long amount;
    private String cardNumber;        // Luôn encrypt trong thực tế
    private String cardHolderName;
    private String expiryDate;        // MM/YY
    private String cvv;               // Luôn encrypt trong thực tế
    private String bankCode;          // Visa, Mastercard, etc.
}

