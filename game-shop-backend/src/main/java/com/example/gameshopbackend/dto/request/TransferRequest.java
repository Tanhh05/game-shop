package com.example.gameshopbackend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferRequest {
    private Long toUserId;
    private Long amount;
}
