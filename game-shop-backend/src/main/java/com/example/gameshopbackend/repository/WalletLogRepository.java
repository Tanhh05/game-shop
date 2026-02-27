package com.example.gameshopbackend.repository;

import com.example.gameshopbackend.entity.WalletLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletLogRepository extends JpaRepository<WalletLog, Long> {
    List<WalletLog> findByWalletIdOrderByCreatedAtDesc(Long walletId);

    boolean existsByBankTransactionId(String bankTransactionId);
}