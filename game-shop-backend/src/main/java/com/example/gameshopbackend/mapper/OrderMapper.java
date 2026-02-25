package com.example.gameshopbackend.mapper;

import com.example.gameshopbackend.dto.response.OrderItemResponse;
import com.example.gameshopbackend.dto.response.OrderResponse;
import com.example.gameshopbackend.entity.Order;
import com.example.gameshopbackend.entity.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "orderDetails", target = "items")
    OrderResponse toResponse(Order order);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.title", target = "productName")
    @Mapping(source = "deliveredKey", target = "key")
    @Mapping(source = "deliveredUsername", target = "username")
    @Mapping(source = "deliveredPassword", target = "password")
    OrderItemResponse toItemResponse(OrderDetail detail);

    List<OrderItemResponse> toItemResponseList(List<OrderDetail> details);
}

