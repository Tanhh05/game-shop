package com.example.gameshopbackend.mapper;

import com.example.gameshopbackend.dto.response.OrderItemResponse;
import com.example.gameshopbackend.dto.response.OrderResponse;
import com.example.gameshopbackend.entity.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {

        OrderResponse res = new OrderResponse();
        res.setId(order.getId());
        res.setUserId(order.getUser().getId());
        res.setUsername(order.getUser().getUsername());
        res.setTotalAmount(order.getTotalAmount());
        res.setStatus(order.getStatus().name());
        res.setCreatedAt(order.getCreatedAt());

        List<OrderItemResponse> items = order.getOrderDetails().stream()
                .map(od -> {
                    OrderItemResponse i = new OrderItemResponse();
                    i.setProductId(od.getProduct().getId());
                    i.setProductName(od.getProduct().getTitle());
                    i.setPrice(od.getPrice());
                    i.setQuantity(od.getQuantity());
                    return i;
                })
                .toList();

        res.setItems(items);
        return res;
    }
}

