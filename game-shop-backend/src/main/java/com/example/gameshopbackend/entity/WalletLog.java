package com.example.gameshopbackend.entity;

import com.example.gameshopbackend.util.WalletLogType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_logs")
@Getter @Setter
public class WalletLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    private WalletLogType type;

    private Long amount;

    private Long balanceBefore;
    private Long balanceAfter;

    private String refId;

    private LocalDateTime createdAt = LocalDateTime.now();
}

