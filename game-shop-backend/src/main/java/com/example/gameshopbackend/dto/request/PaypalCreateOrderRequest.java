package com.example.gameshopbackend.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaypalCreateOrderRequest {
    @NotNull(message = "amount is required")
    @Positive(message = "amount must be greater than 0")
    private Long amount;
}
