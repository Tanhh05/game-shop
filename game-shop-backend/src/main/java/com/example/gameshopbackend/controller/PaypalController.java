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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }
        if (request == null || request.getAmount() == null || request.getAmount() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid amount"));
        }

        try {
            Map<String, Object> result = paypalService.createOrder(request.getAmount());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create PayPal order", "detail", ex.getMessage()));
        }
    }

    @PostMapping("/capture")
    public ResponseEntity<?> capture(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam String orderId,
            @RequestParam Long amount
    ) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }
        if (orderId == null || orderId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing orderId"));
        }
        if (amount == null || amount <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid amount"));
        }

        try {
            boolean captured = paypalService.captureOrder(orderId);
            if (!captured) {
                return ResponseEntity.badRequest().body(Map.of("error", "PayPal capture failed or not completed"));
            }
            // top up only when capture completed
            try {
                walletService.topupByPaypal(user.getId(), amount);
            } catch (Exception ex) {
                // if wallet topup fails, return 500 and include detail
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Failed to top up wallet", "detail", ex.getMessage()));
            }
            return ResponseEntity.ok(Map.of("message", "Nạp tiền PayPal thành công"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "PayPal capture error", "detail", ex.getMessage()));
        }
    }

}
