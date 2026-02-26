package com.example.gameshopbackend.service;

import com.example.gameshopbackend.dto.request.CreateOrderRequest;
import com.example.gameshopbackend.dto.response.OrderResponse;
import com.example.gameshopbackend.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderService {
    @Transactional(rollbackFor = Exception.class)
    OrderResponse buyNow(Long userId, CreateOrderRequest request);

    Page<OrderResponse> getPurchaseHistory(
            Long userId,
            int page,
            int size
    );
}
