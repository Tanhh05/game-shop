package com.example.gameshopbackend.controller;

import com.example.gameshopbackend.dto.request.CreateOrderRequest;
import com.example.gameshopbackend.entity.Order;
import com.example.gameshopbackend.mapper.OrderMapper;
import com.example.gameshopbackend.repository.OrderRepository;
import com.example.gameshopbackend.service.OrderService;
import com.example.gameshopbackend.util.OrderStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;

    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestParam Long userId,
            @RequestBody CreateOrderRequest request
    ) {
        Order order = orderService.createOrder(userId, request);
        return ResponseEntity.ok(orderMapper.toResponse(order));
    }

    @GetMapping("/ping")
    public String ping() {
        return "BE is running OK bây bi nháaa🚀";
    }

    @DeleteMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok("Order cancelled");
    }

    @GetMapping("/cart")
    public ResponseEntity<?> getCart(@RequestParam Long userId) {
        List<Order> carts = orderRepository
                .findAllByUserIdAndStatus(userId, OrderStatus.PENDING);

        return ResponseEntity.ok(
                carts.stream().map(orderMapper::toResponse).toList()
        );
    }

    @PostMapping("/buy-now")
    public ResponseEntity<?> buyNow(
            @RequestParam Long userId,
            @RequestBody CreateOrderRequest request
    ) {
        Order order = orderService.buyNow(userId, request);
        return ResponseEntity.ok(orderMapper.toResponse(order));
    }

}
