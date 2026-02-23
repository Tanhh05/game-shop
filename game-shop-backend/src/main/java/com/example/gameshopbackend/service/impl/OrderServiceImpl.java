package com.example.gameshopbackend.service.impl;

import com.example.gameshopbackend.dto.request.CreateOrderRequest;
import com.example.gameshopbackend.dto.request.OrderItemRequest;
import com.example.gameshopbackend.entity.Order;
import com.example.gameshopbackend.entity.OrderDetail;
import com.example.gameshopbackend.entity.Product;
import com.example.gameshopbackend.entity.User;
import com.example.gameshopbackend.exception.InsufficientBalanceException;
import com.example.gameshopbackend.exception.OutOfStockException;
import com.example.gameshopbackend.repository.OrderRepository;
import com.example.gameshopbackend.repository.ProductRepository;
import com.example.gameshopbackend.repository.UserRepository;
import com.example.gameshopbackend.service.DeliveryService;
import com.example.gameshopbackend.service.OrderService;
import com.example.gameshopbackend.service.WalletService;
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
    private final WalletService walletService;
    private final DeliveryService deliveryService;

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

    @Transactional
    @Override
    public Order buyNow(Long userId, CreateOrderRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(0L);

        order = orderRepository.save(order);

        long totalAmount = 0L;

        // Kiểm tra hàng có sẵn và tính toán tổng tiền
        for (OrderItemRequest item : request.getItems()) {

            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Kiểm tra có đủ hàng không
            if (!deliveryService.hasStock(product.getId(), item.getQuantity())) {
                throw new OutOfStockException("Sản phẩm " + product.getTitle() + " không đủ hàng. Chỉ còn " +
                        deliveryService.getAvailableStock(product.getId()) + " sản phẩm");
            }

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setPrice(product.getPrice());
            detail.setQuantity(item.getQuantity());

            order.getOrderDetails().add(detail);

            totalAmount += product.getPrice() * item.getQuantity();
        }

        order.setTotalAmount(totalAmount);

        // Trừ tiền ví (nếu hết tiền sẽ throw exception)
        try {
            walletService.pay(userId, totalAmount, order.getId().toString());
        } catch (Exception ex) {
            throw new InsufficientBalanceException("Ví không đủ tiền. Cần: " + totalAmount);
        }

        // Giao hàng tự động
        try {
            deliveryService.deliverOrder(order);
        } catch (Exception ex) {
            // Nếu giao hàng thất bại, hoàn tiền
            walletService.refund(userId, totalAmount, "Hoàn tiền do giao hàng thất bại - " + order.getId());
            throw ex;
        }

        order.setStatus(OrderStatus.SUCCESS);

        return orderRepository.save(order);
    }




}

