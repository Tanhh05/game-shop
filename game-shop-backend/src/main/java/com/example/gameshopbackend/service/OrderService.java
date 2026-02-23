package com.example.gameshopbackend.service;

import com.example.gameshopbackend.dto.request.CreateOrderRequest;
import com.example.gameshopbackend.entity.Order;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService {
    Order createOrder(Long userId, CreateOrderRequest request);

    @Transactional
    void cancelOrder(Long orderId);

    @Transactional
    Order buyNow(Long userId, CreateOrderRequest request);
}
