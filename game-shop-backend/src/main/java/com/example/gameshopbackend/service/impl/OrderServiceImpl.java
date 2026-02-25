package com.example.gameshopbackend.service.impl;

import com.example.gameshopbackend.dto.request.CreateOrderRequest;
import com.example.gameshopbackend.dto.request.OrderItemRequest;
import com.example.gameshopbackend.dto.response.DeliveryItemResponse;
import com.example.gameshopbackend.dto.response.OrderItemResponse;
import com.example.gameshopbackend.dto.response.OrderResponse;
import com.example.gameshopbackend.entity.GameAccount;
import com.example.gameshopbackend.entity.GameKey;
import com.example.gameshopbackend.entity.Order;
import com.example.gameshopbackend.entity.OrderDetail;
import com.example.gameshopbackend.entity.Product;
import com.example.gameshopbackend.entity.ProductPackage;
import com.example.gameshopbackend.entity.User;
import com.example.gameshopbackend.exception.OutOfStockException;
import com.example.gameshopbackend.mapper.OrderMapper;
import com.example.gameshopbackend.repository.GameAccountRepository;
import com.example.gameshopbackend.repository.GameKeyRepository;
import com.example.gameshopbackend.repository.OrderRepository;
import com.example.gameshopbackend.repository.ProductPackageRepository;
import com.example.gameshopbackend.repository.ProductRepository;
import com.example.gameshopbackend.repository.UserRepository;
import com.example.gameshopbackend.service.DeliveryService;
import com.example.gameshopbackend.service.OrderService;
import com.example.gameshopbackend.service.WalletService;
import com.example.gameshopbackend.util.ItemStatus;
import com.example.gameshopbackend.util.OrderStatus;
import com.example.gameshopbackend.util.ProductType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final WalletService walletService;
    private final DeliveryService deliveryService;
    private final GameKeyRepository gameKeyRepository;
    private final GameAccountRepository gameAccountRepository;
    private final ProductPackageRepository productPackageRepository;
    private final OrderMapper orderMapper;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public OrderResponse buyNow(Long userId, CreateOrderRequest request) {

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Đơn hàng không có sản phẩm");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        long totalAmount = 0L;

        // Lưu product và package tạm để tránh query lại
        Map<Long, Product> productMap = new HashMap<>();
        Map<Long, ProductPackage> packageMap = new HashMap<>();

        // ==========================
        // 1️⃣ VALIDATE & TÍNH TIỀN
        // ==========================

        for (OrderItemRequest item : request.getItems()) {

            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new RuntimeException("Số lượng không hợp lệ");
            }

            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            productMap.put(product.getId(), product);

            if (product.getType() == ProductType.KEY) {

                if (item.getPackageId() == null) {
                    throw new RuntimeException("KEY phải chọn gói");
                }

                ProductPackage pkg = productPackageRepository
                        .findById(item.getPackageId())
                        .orElseThrow(() -> new RuntimeException("Package not found"));

                packageMap.put(pkg.getId(), pkg);

                long available =
                        gameKeyRepository.countByProductIdAndStatus(
                                product.getId(),
                                ItemStatus.AVAILABLE);

                if (available < item.getQuantity()) {
                    throw new RuntimeException("Không đủ key trong kho");
                }

                totalAmount += pkg.getPrice() * item.getQuantity();
            }

            else if (product.getType() == ProductType.ACCOUNT) {

                long available =
                        gameAccountRepository.countByProductIdAndStatus(
                                product.getId(),
                                ItemStatus.AVAILABLE);

                if (available < item.getQuantity()) {
                    throw new RuntimeException("Không đủ account trong kho");
                }

                totalAmount += product.getPrice() * item.getQuantity();
            }

            else {
                throw new RuntimeException("Loại sản phẩm không hỗ trợ");
            }
        }

        // ==========================
        // 2️⃣ TẠO ORDER
        // ==========================

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(totalAmount);

        order = orderRepository.save(order);

        // ==========================
// 3️⃣ TẠO ORDER DETAILS
// ==========================

        for (OrderItemRequest item : request.getItems()) {

            Product product = productMap.get(item.getProductId());

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());

            if (product.getType() == ProductType.KEY) {

                ProductPackage pkg = packageMap.get(item.getPackageId());

                detail.setProductPackage(pkg);
                detail.setPrice(pkg.getPrice());

            } else {
                detail.setPrice(product.getPrice());
            }

            order.getOrderDetails().add(detail);
        }

// 🔥 QUAN TRỌNG
        order = orderRepository.save(order);

        // ==========================
        // 4️⃣ TRỪ TIỀN
        // ==========================

        walletService.pay(userId, totalAmount, order.getId().toString());

        // ==========================
        // 5️⃣ GIAO HÀNG
        // ==========================

        deliveryService.deliverOrder(order);

        order.setStatus(OrderStatus.SUCCESS);
        orderRepository.save(order);

        return orderMapper.toResponse(order);
    }

    @Override
    public List<OrderResponse> getPurchaseHistory(Long userId) {

        List<Order> orders = orderRepository
                .findAllByUserIdAndStatusOrderByCreatedAtDesc(
                        userId,
                        OrderStatus.SUCCESS
                );

        return orders.stream().map(order -> {

            OrderResponse response = new OrderResponse();
            response.setId(order.getId());
            response.setUserId(order.getUser().getId());
            response.setUsername(order.getUser().getUsername());
            response.setTotalAmount(order.getTotalAmount());
            response.setStatus(order.getStatus().name());
            response.setCreatedAt(order.getCreatedAt());

            List<OrderItemResponse> itemResponses = new ArrayList<>();

            for (OrderDetail detail : order.getOrderDetails()) {

                OrderItemResponse itemResponse = new OrderItemResponse();
                itemResponse.setProductId(detail.getProduct().getId());
                itemResponse.setProductName(detail.getProduct().getTitle());
                itemResponse.setQuantity(detail.getQuantity());
                itemResponse.setPrice(detail.getPrice());

                // 🔥 Lấy KEY đã bán
                List<GameKey> keys = gameKeyRepository
                        .findByOrderDetailId(detail.getId());

                if (!keys.isEmpty()) {
                    itemResponse.setKey(keys.get(0).getLicenseKey());
                }

                // 🔥 Lấy ACCOUNT đã bán
                List<GameAccount> accounts = gameAccountRepository
                        .findByOrderDetailId(detail.getId());

                if (!accounts.isEmpty()) {
                    itemResponse.setUsername(accounts.get(0).getUsername());
                    itemResponse.setPassword(accounts.get(0).getPassword());
                }

                itemResponses.add(itemResponse);
            }

            response.setItems(itemResponses);
            return response;

        }).toList();
    }
}

