package com.example.gameshopbackend.entity;


import com.example.gameshopbackend.util.ItemStatus;
import jakarta.persistence.Column;
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
@Getter
@Setter
public class GameKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String licenseKey;

    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    @Column(name = "rented_by_user_id")
    private Long rentedByUserId;

    private LocalDateTime expiredAt;

    @ManyToOne
    private Product product;

    @ManyToOne
    private OrderDetail orderDetail;
}

