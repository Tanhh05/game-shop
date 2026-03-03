package com.example.gameshopbackend.repository;

import com.example.gameshopbackend.entity.PaymentTransaction;
import com.example.gameshopbackend.util.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    /**
     * Lấy tất cả giao dịch của một user
     */
    Page<PaymentTransaction> findByUserId(Long userId, Pageable pageable);

    /**
     * Lấy giao dịch theo status
     */
    List<PaymentTransaction> findByStatus(PaymentStatus status);

    /**
     * Đếm giao dịch thành công của user
     */
    Long countByUserIdAndStatus(Long userId, PaymentStatus status);

    /**
     * Lấy tất cả giao dịch trong khoảng thời gian
     */
    List<PaymentTransaction> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Tìm giao dịch theo provider (PayPal, Stripe, v.v.)
     */
    List<PaymentTransaction> findByProviderAndStatus(String provider, PaymentStatus status);

    boolean existsByPaypalOrderIdAndStatus(String paypalOrderId, PaymentStatus status);
}

