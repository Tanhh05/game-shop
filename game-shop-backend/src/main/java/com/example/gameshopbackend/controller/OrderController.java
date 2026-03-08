package com.example.gameshopbackend.controller;

import com.example.gameshopbackend.dto.request.CreateOrderRequest;
import com.example.gameshopbackend.dto.response.OrderResponse;
import com.example.gameshopbackend.jwt.UserPrincipal;
import com.example.gameshopbackend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/ping")
    public String ping() {
        return "BE is running OK bây bi nháaa🚀";
    }


    @GetMapping("/history")
    public ResponseEntity<Page<OrderResponse>> getPurchaseHistory(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(
                orderService.getPurchaseHistory(principal.getId(), page, size)
        );
    }

    @PostMapping("/buy-now")
    public ResponseEntity<?> buyNow(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) Long userId,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized"));
        }

        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        if (!isAdmin && userId != null && !principal.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Bạn không thể mua hàng thay cho user khác"));
        }

        Long effectiveUserId = isAdmin
                ? (userId != null ? userId : principal.getId())
                : principal.getId();

        OrderResponse response = orderService.buyNow(effectiveUserId, request);
        return ResponseEntity.ok(response);
    }

}
