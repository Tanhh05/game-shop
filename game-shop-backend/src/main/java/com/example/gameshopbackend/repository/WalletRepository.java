package com.example.gameshopbackend.repository;

import com.example.gameshopbackend.entity.User;
import com.example.gameshopbackend.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserId(Long userId);

}