package com.example.gameshopbackend.controller;

import com.example.gameshopbackend.dto.request.CreateOrderRequest;
import com.example.gameshopbackend.dto.response.OrderResponse;
import com.example.gameshopbackend.entity.Order;
import com.example.gameshopbackend.mapper.OrderMapper;
import com.example.gameshopbackend.repository.OrderRepository;
import com.example.gameshopbackend.repository.UserRepository;
import com.example.gameshopbackend.service.OrderService;
import com.example.gameshopbackend.util.OrderStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final UserRepository userRepository;

    @GetMapping("/ping")
    public String ping() {
        return "BE is running OK bây bi nháaa🚀";
    }


    @GetMapping("/history")
    public ResponseEntity<Page<OrderResponse>> getPurchaseHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {

        String username = userDetails.getUsername();

        Long userId = userRepository
                .findByUsername(username)
                .orElseThrow()
                .getId();

        return ResponseEntity.ok(
                orderService.getPurchaseHistory(userId, page, size)
        );
    }

    @PostMapping("/buy-now")
    public ResponseEntity<OrderResponse> buyNow(
            @RequestParam Long userId,
            @RequestBody CreateOrderRequest request
    ) {
        OrderResponse response = orderService.buyNow(userId, request);
        return ResponseEntity.ok(response);
    }

}
