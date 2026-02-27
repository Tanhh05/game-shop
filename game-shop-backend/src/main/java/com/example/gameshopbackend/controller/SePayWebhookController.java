package com.example.gameshopbackend.controller;

import com.example.gameshopbackend.entity.User;
import com.example.gameshopbackend.entity.Wallet;
import com.example.gameshopbackend.entity.WalletLog;
import com.example.gameshopbackend.repository.UserRepository;
import com.example.gameshopbackend.repository.WalletLogRepository;
import com.example.gameshopbackend.repository.WalletRepository;
import com.example.gameshopbackend.util.WalletLogType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class SePayWebhookController {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final WalletLogRepository walletLogRepository;

    @PostMapping("/sepay")
    @Transactional
    public ResponseEntity<String> handleSePayWebhook(
            @RequestBody Map<String, Object> payload) {

        try {

            System.out.println("WEBHOOK RECEIVED: " + payload);

            String transactionId = payload.get("id").toString();
            Long amount = Long.parseLong(payload.get("amount").toString());
            String description = payload.get("description").toString().trim();

            if (walletLogRepository.existsByBankTransactionId(transactionId)) {
                return ResponseEntity.ok("Already processed");
            }

            Optional<User> optionalUser =
                    userRepository.findByDepositCodeContaining(description);

            if (optionalUser.isEmpty()) {
                System.out.println("Không tìm thấy user với description: " + description);
                return ResponseEntity.ok("User not found");
            }

            User user = optionalUser.get();

            Wallet wallet = walletRepository
                    .findByUserIdForUpdate(user.getId())
                    .orElseThrow(() -> new RuntimeException("Wallet not found"));

            Long before = wallet.getBalance();
            Long after = before + amount;

            wallet.setBalance(after);
            wallet.setUpdatedAt(LocalDateTime.now());
            walletRepository.save(wallet);

            WalletLog log = new WalletLog();
            log.setWallet(wallet);
            log.setType(WalletLogType.TOPUP);
            log.setAmount(amount);
            log.setBalanceBefore(before);
            log.setBalanceAfter(after);
            log.setRefId(description);
            log.setBankTransactionId(transactionId);
            log.setCreatedAt(LocalDateTime.now());

            walletLogRepository.save(log);

            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("ERROR");
        }
    }

    @GetMapping("/wallet/deposit-info")
    public ResponseEntity<?> depositInfo(Authentication auth) {

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String bankCode = "MBBank";  // đúng format SePay yêu cầu
        String accountNumber = "0346771322";
        String accountName = "PHAN TUAN ANH";

        String depositContent = user.getDepositCode();

        String qrUrl = String.format(
                "https://qr.sepay.vn/img?acc=%s&bank=%s&amount=&des=%s&template=compact",
                accountNumber,
                bankCode,
                depositContent
        );

        return ResponseEntity.ok(Map.of(
                "bankName", "MBBank",
                "accountNumber", accountNumber,
                "accountName", accountName,
                "depositContent", depositContent,
                "qrUrl", qrUrl
        ));
    }
}