package com.example.gameshopbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AdminOrderResponse {
    private Long id;
    private Long userId;
    private String username;
    private Long totalAmount;
    private String status;
    private Integer itemCount;
    private LocalDateTime createdAt;
}

