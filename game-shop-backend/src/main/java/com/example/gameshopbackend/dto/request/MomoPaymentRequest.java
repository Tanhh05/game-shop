package com.example.gameshopbackend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MomoPaymentRequest {
    private Long userId;
    private Long amount;
    private String phoneNumber;      // Số điện thoại Momo
    private String description;      // Nội dung thanh toán
}

