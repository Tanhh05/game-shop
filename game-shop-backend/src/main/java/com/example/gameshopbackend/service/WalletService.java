package com.example.gameshopbackend.service;

import com.example.gameshopbackend.entity.Wallet;
import com.example.gameshopbackend.entity.WalletLog;
import com.example.gameshopbackend.repository.WalletLogRepository;
import com.example.gameshopbackend.repository.WalletRepository;
import com.example.gameshopbackend.util.WalletLogType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletLogRepository walletLogRepository;

    // Xem số dư
    public Long getBalance(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Ví không tồn tại"))
                .getBalance();
    }

    // Lịch sử ví
    public List<WalletLog> getLogs(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Ví không tồn tại"));
        return walletLogRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId());
    }

    // Nạp tiền
    public void topup(Long userId, Long amount) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Ví không tồn tại"));

        Long before = wallet.getBalance();
        wallet.setBalance(before + amount);
        wallet.setUpdatedAt(LocalDateTime.now());

        walletRepository.save(wallet);

        saveLog(wallet, WalletLogType.TOPUP, amount, before, wallet.getBalance(), null);
    }

    // Mua hàng
    public void pay(Long userId, Long amount, String orderId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Ví không tồn tại"));

        if (wallet.getBalance() < amount) {
            throw new RuntimeException("Số dư không đủ");
        }

        Long before = wallet.getBalance();
        wallet.setBalance(before - amount);

        walletRepository.save(wallet);

        saveLog(wallet, WalletLogType.BUY, amount, before, wallet.getBalance(), orderId);
    }

    // Refund
    public void refund(Long userId, Long amount, String orderId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Ví không tồn tại"));

        Long before = wallet.getBalance();
        wallet.setBalance(before + amount);

        walletRepository.save(wallet);

        saveLog(wallet, WalletLogType.REFUND, amount, before, wallet.getBalance(), orderId);
    }

    // Chuyển tiền
    public void transfer(Long fromUserId, Long toUserId, Long amount) {

        Wallet from = walletRepository.findByUserId(fromUserId)
                .orElseThrow(() -> new RuntimeException("Ví nguồn không tồn tại"));

        Wallet to = walletRepository.findByUserId(toUserId)
                .orElseThrow(() -> new RuntimeException("Ví đích không tồn tại"));

        if (from.getBalance() < amount) {
            throw new RuntimeException("Số dư không đủ");
        }

        Long fromBefore = from.getBalance();
        Long toBefore = to.getBalance();

        from.setBalance(fromBefore - amount);
        to.setBalance(toBefore + amount);

        walletRepository.save(from);
        walletRepository.save(to);

        saveLog(from, WalletLogType.TRANSFER, amount, fromBefore, from.getBalance(), "TO_" + toUserId);
        saveLog(to, WalletLogType.TRANSFER, amount, toBefore, to.getBalance(), "FROM_" + fromUserId);
    }

    private void saveLog(Wallet wallet,
                         WalletLogType type,
                         Long amount,
                         Long before,
                         Long after,
                         String refId) {

        WalletLog log = new WalletLog();
        log.setWallet(wallet);
        log.setType(type);
        log.setAmount(amount);
        log.setBalanceBefore(before);
        log.setBalanceAfter(after);
        log.setRefId(refId);
        log.setCreatedAt(LocalDateTime.now());

        walletLogRepository.save(log);
    }

    @Transactional
    public void topupByPaypal(Long userId, Long amount) {

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ví"));

        wallet.setBalance(wallet.getBalance() + amount);

        walletRepository.save(wallet);
    }

}
