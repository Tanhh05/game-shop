package com.example.gameshopbackend.controller;

import com.example.gameshopbackend.dto.request.PaypalCreateOrderRequest;
import com.example.gameshopbackend.security.UserPrincipal;
import com.example.gameshopbackend.service.PaypalService;
import com.example.gameshopbackend.service.WalletService;
import lombok.RequiredArgsConstructor;
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
    public Map<String, Object> createOrder(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody PaypalCreateOrderRequest request
    ) {
        return paypalService.createOrder(request.getAmount());
    }

    @PostMapping("/capture")
    public String capture(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam String orderId,
            @RequestParam Long amount
    ) {
        paypalService.captureOrder(orderId);

        walletService.topupByPaypal(user.getId(), amount);

        return "Nạp tiền PayPal thành công";
    }

}
