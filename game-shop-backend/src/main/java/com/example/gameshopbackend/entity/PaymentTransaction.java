package com.example.gameshopbackend.entity;

import com.example.gameshopbackend.util.PaymentMethod;
import com.example.gameshopbackend.util.PaymentStatus;
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
@Table(name = "payment_transactions")
@Getter @Setter
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Long amount;
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String provider;  // e.g., "PAYPAL", "STRIPE", "MOMO", "ATM"
    private String paypalOrderId;
    private String paypalCaptureId;

    private LocalDateTime createdAt = LocalDateTime.now();
}
