package com.example.gameshopbackend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaypalCreateOrderRequest {
    private Long amount;
}
