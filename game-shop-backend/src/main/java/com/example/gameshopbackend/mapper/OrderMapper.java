package com.example.gameshopbackend.mapper;

import com.example.gameshopbackend.dto.response.OrderItemResponse;
import com.example.gameshopbackend.dto.response.OrderResponse;
import com.example.gameshopbackend.entity.Order;
import com.example.gameshopbackend.entity.OrderDetail;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        if (order == null) {
            return null;
        }
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        if (order.getUser() != null) {
            response.setUserId(order.getUser().getId());
            response.setUsername(order.getUser().getUsername());
        }
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus() != null ? order.getStatus().name() : null);
        response.setCreatedAt(order.getCreatedAt());
        response.setItems(toItemResponseList(order.getOrderDetails()));
        return response;
    }

    public OrderItemResponse toItemResponse(OrderDetail detail) {
        if (detail == null) {
            return null;
        }
        OrderItemResponse response = new OrderItemResponse();
        if (detail.getProduct() != null) {
            response.setProductId(detail.getProduct().getId());
            response.setProductName(detail.getProduct().getTitle());
        }
        response.setQuantity(detail.getQuantity());
        response.setPrice(detail.getPrice());
        response.setKey(detail.getDeliveredKey());
        response.setUsername(detail.getDeliveredUsername());
        response.setPassword(detail.getDeliveredPassword());
        return response;
    }

    public List<OrderItemResponse> toItemResponseList(List<OrderDetail> details) {
        if (details == null) {
            return null;
        }
        List<OrderItemResponse> responses = new ArrayList<>(details.size());
        for (OrderDetail detail : details) {
            responses.add(toItemResponse(detail));
        }
        return responses;
    }
}
