package com.example.gameshopbackend.controller;

import com.example.gameshopbackend.dto.request.PaypalCreateOrderRequest;
import com.example.gameshopbackend.security.UserPrincipal;
import com.example.gameshopbackend.service.PaypalService;
import com.example.gameshopbackend.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/paypal")
@RequiredArgsConstructor
public class PaypalController {

    private final PaypalService paypalService;
    private final WalletService walletService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody PaypalCreateOrderRequest request
    ) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized"));
        }

        if (request == null || request.getAmount() == null || request.getAmount() <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid amount"));
        }

        Map<String, Object> result =
                paypalService.createOrder(request.getAmount());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/capture")
    public ResponseEntity<?> capture(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody Map<String, String> request
    ) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized"));
        }

        String orderId = request.get("orderId");

        if (orderId == null || orderId.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "orderId is required"));
        }

        BigDecimal amountUsd = paypalService.captureOrder(orderId);

        if (amountUsd == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Capture failed"));
        }

        long amountVnd = paypalService.convertUsdToVnd(amountUsd);

        walletService.topupByPaypal(user.getId(), amountVnd);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Nạp tiền thành công",
                        "amountVnd", amountVnd
                )
        );
    }
}