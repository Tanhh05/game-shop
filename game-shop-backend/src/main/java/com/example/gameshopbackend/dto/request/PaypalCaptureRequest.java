package com.example.gameshopbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaypalCaptureRequest {
    @NotBlank(message = "orderId is required")
    private String orderId;
}
