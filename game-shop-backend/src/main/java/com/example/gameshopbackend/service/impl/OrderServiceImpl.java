package com.example.gameshopbackend.service.impl;

import com.example.gameshopbackend.dto.request.CreateOrderRequest;
import com.example.gameshopbackend.dto.request.OrderItemRequest;
import com.example.gameshopbackend.entity.Order;
import com.example.gameshopbackend.entity.OrderDetail;
import com.example.gameshopbackend.entity.Product;
import com.example.gameshopbackend.entity.User;
import com.example.gameshopbackend.repository.OrderRepository;
import com.example.gameshopbackend.repository.ProductRepository;
import com.example.gameshopbackend.repository.UserRepository;
import com.example.gameshopbackend.service.OrderService;
import com.example.gameshopbackend.util.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public Order createOrder(Long userId, CreateOrderRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository
                .findFirstByUserIdAndStatus(userId, OrderStatus.PENDING)
                .orElseGet(() -> {
                    Order o = new Order();
                    o.setUser(user);
                    o.setStatus(OrderStatus.PENDING);
                    o.setTotalAmount(0L);
                    return orderRepository.save(o);
                });

        long totalAmount = order.getTotalAmount();

        for (OrderItemRequest item : request.getItems()) {

            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            Optional<OrderDetail> existed = order.getOrderDetails().stream()
                    .filter(d -> d.getProduct().getId().equals(product.getId()))
                    .findFirst();

            if (existed.isPresent()) {
                OrderDetail d = existed.get();
                d.setQuantity(d.getQuantity() + item.getQuantity());
            } else {
                OrderDetail d = new OrderDetail();
                d.setOrder(order);
                d.setProduct(product);
                d.setPrice(product.getPrice());
                d.setQuantity(item.getQuantity());
                order.getOrderDetails().add(d);
            }

            totalAmount += product.getPrice() * item.getQuantity();
        }

        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }

    @Transactional
    @Override
    public void cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Only PENDING order can be cancelled");
        }

        order.setStatus(OrderStatus.FAILED);

        order.getOrderDetails().clear();
        order.setTotalAmount(0L);

        orderRepository.save(order);
    }



}

