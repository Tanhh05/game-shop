package com.example.gameshopbackend.controller;

import com.example.gameshopbackend.dto.request.ATMPaymentRequest;
import com.example.gameshopbackend.dto.request.CardPaymentRequest;
import com.example.gameshopbackend.dto.request.MomoPaymentRequest;
import com.example.gameshopbackend.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final WalletService walletService;

    /**
     * Thanh toán bằng thẻ (Card)
     * Trong thực tế nên tích hợp với Stripe, VNPay, Payoo, v.v.
     */
    @PostMapping("/card")
    public ResponseEntity<?> payByCard(@Valid @RequestBody CardPaymentRequest request) {
        try {
            if (request.getUserId() == null || request.getAmount() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "userId và amount là bắt buộc"));
            }
            if (request.getAmount() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Số tiền phải lớn hơn 0"));
            }

            // TODO: Tích hợp với payment gateway (Stripe, VNPay, etc)
            // Ở đây chỉ là placeholder
            String transactionId = "CARD_" + UUID.randomUUID().toString();

            // Nạp tiền vào ví
            walletService.topup(request.getUserId(), request.getAmount());

            return ResponseEntity.ok(Map.of(
                    "message", "Thanh toán bằng thẻ thành công",
                    "transactionId", transactionId,
                    "amount", request.getAmount(),
                    "status", "SUCCESS"
            ));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Thanh toán thất bại", "detail", ex.getMessage()));
        }
    }

    /**
     * Thanh toán bằng chuyển khoản ATM/Ngân hàng
     */
    @PostMapping("/atm")
    public ResponseEntity<?> payByATM(@Valid @RequestBody ATMPaymentRequest request) {
        try {
            if (request.getUserId() == null || request.getAmount() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "userId và amount là bắt buộc"));
            }
            if (request.getAmount() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Số tiền phải lớn hơn 0"));
            }

            // TODO: Tích hợp với gateway chuyển khoản ngân hàng
            // Cần verify transfer thật từ ngân hàng
            String transactionId = "ATM_" + UUID.randomUUID().toString();

            // Trong thực tế cần confirm từ ngân hàng trước khi nạp tiền
            return ResponseEntity.ok(Map.of(
                    "message", "Yêu cầu thanh toán ATM được tạo",
                    "transactionId", transactionId,
                    "amount", request.getAmount(),
                    "status", "PENDING",
                    "instruction", "Vui lòng chuyển khoản đến tài khoản được cung cấp"
            ));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Tạo giao dịch ATM thất bại", "detail", ex.getMessage()));
        }
    }

    /**
     * Thanh toán bằng Momo
     */
    @PostMapping("/momo")
    public ResponseEntity<?> payByMomo(@Valid @RequestBody MomoPaymentRequest request) {
        try {
            if (request.getUserId() == null || request.getAmount() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "userId và amount là bắt buộc"));
            }
            if (request.getAmount() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Số tiền phải lớn hơn 0"));
            }

            // TODO: Tích hợp với Momo API
            String transactionId = "MOMO_" + UUID.randomUUID().toString();

            // Nạp tiền vào ví
            walletService.topup(request.getUserId(), request.getAmount());

            return ResponseEntity.ok(Map.of(
                    "message", "Thanh toán Momo thành công",
                    "transactionId", transactionId,
                    "amount", request.getAmount(),
                    "status", "SUCCESS"
            ));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Thanh toán Momo thất bại", "detail", ex.getMessage()));
        }
    }
}
