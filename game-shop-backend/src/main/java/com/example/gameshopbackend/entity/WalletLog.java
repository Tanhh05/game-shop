package com.example.gameshopbackend.entity;

import com.example.gameshopbackend.util.WalletLogType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_logs",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "bank_transaction_id")
        })
@Getter
@Setter
public class WalletLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletLogType type;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private Long balanceBefore;

    @Column(nullable = false)
    private Long balanceAfter;

    private String refId; // nap11848

    @Column(name = "bank_transaction_id", unique = true)
    private String bankTransactionId; // id từ SePay

    private LocalDateTime createdAt = LocalDateTime.now();
}

