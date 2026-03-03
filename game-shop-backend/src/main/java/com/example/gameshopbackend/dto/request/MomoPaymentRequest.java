package com.example.gameshopbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MomoPaymentRequest {
    @NotNull(message = "userId is required")
    @Positive(message = "userId must be greater than 0")
    private Long userId;

    @NotNull(message = "amount is required")
    @Positive(message = "amount must be greater than 0")
    private Long amount;

    @NotBlank(message = "phoneNumber is required")
    private String phoneNumber;      // Số điện thoại Momo

    private String description;      // Nội dung thanh toán
}
