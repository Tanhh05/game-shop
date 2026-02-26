package com.example.gameshopbackend.repository;

import com.example.gameshopbackend.entity.Order;
import com.example.gameshopbackend.util.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByUserIdAndStatus(
            Long userId,
            OrderStatus status,
            Pageable pageable
    );

    List<Order> findAllByUserIdAndStatusOrderByCreatedAtDesc(
            Long userId,
            OrderStatus status
    );

    List<Order> findByUserId(Long userId);
}