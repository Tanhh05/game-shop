package com.example.gameshopbackend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ATMPaymentRequest {
    private Long userId;
    private Long amount;
    private String bankCode;         // Tên ngân hàng
    private String bankAccountNumber;
    private String bankAccountName;
    private String description;      // Nội dung chuyển khoản
}

