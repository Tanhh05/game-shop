package com.example.gameshopbackend.controller;

import com.example.gameshopbackend.dto.request.PaypalCreateOrderRequest;
import com.example.gameshopbackend.dto.request.PaypalCaptureRequest;
import com.example.gameshopbackend.entity.PaymentTransaction;
import com.example.gameshopbackend.entity.User;
import com.example.gameshopbackend.repository.PaymentTransactionRepository;
import com.example.gameshopbackend.repository.UserRepository;
import com.example.gameshopbackend.security.UserPrincipal;
import com.example.gameshopbackend.service.PaypalService;
import com.example.gameshopbackend.service.WalletService;
import com.example.gameshopbackend.util.PaymentMethod;
import com.example.gameshopbackend.util.PaymentStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/paypal")
@RequiredArgsConstructor
public class PaypalController {

    private final PaypalService paypalService;
    private final WalletService walletService;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final UserRepository userRepository;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody PaypalCreateOrderRequest request
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
    @Transactional
    public ResponseEntity<?> capture(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody PaypalCaptureRequest request
    ) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized"));
        }

        String orderId = request.getOrderId();

        User dbUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        PaymentTransaction tx = new PaymentTransaction();
        tx.setUser(dbUser);
        tx.setAmount(0L);
        tx.setCurrency("VND");
        tx.setMethod(PaymentMethod.PAYPAL);
        tx.setStatus(PaymentStatus.CREATED);
        tx.setProvider("PAYPAL");
        tx.setPaypalOrderId(orderId);

        try {
            paymentTransactionRepository.save(tx);
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.ok(Map.of(
                    "message", "Giao dịch đã được xử lý trước đó",
                    "orderId", orderId
            ));
        }

        BigDecimal amountUsd = paypalService.captureOrder(orderId);

        if (amountUsd == null) {
            tx.setStatus(PaymentStatus.FAILED);
            paymentTransactionRepository.save(tx);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Capture failed"));
        }

        long amountVnd = paypalService.convertUsdToVnd(amountUsd);

        walletService.topupByPaypal(user.getId(), amountVnd);
        tx.setAmount(amountVnd);
        tx.setStatus(PaymentStatus.SUCCESS);
        paymentTransactionRepository.save(tx);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Nạp tiền thành công",
                        "amountVnd", amountVnd
                )
        );
    }
}
