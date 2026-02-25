package com.example.gameshopbackend.service.impl;

import com.example.gameshopbackend.dto.response.DeliveryItemResponse;
import com.example.gameshopbackend.entity.GameAccount;
import com.example.gameshopbackend.entity.GameKey;
import com.example.gameshopbackend.entity.Order;
import com.example.gameshopbackend.entity.OrderDetail;
import com.example.gameshopbackend.entity.Product;
import com.example.gameshopbackend.entity.ProductPackage;
import com.example.gameshopbackend.exception.DeliveryException;
import com.example.gameshopbackend.exception.OutOfStockException;
import com.example.gameshopbackend.repository.GameAccountRepository;
import com.example.gameshopbackend.repository.GameKeyRepository;
import com.example.gameshopbackend.repository.ProductRepository;
import com.example.gameshopbackend.service.DeliveryService;
import com.example.gameshopbackend.util.ItemStatus;
import com.example.gameshopbackend.util.ProductType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final GameKeyRepository gameKeyRepository;
    private final GameAccountRepository gameAccountRepository;
    private final ProductRepository productRepository;

    // ==============================
    // 1. Deliver full order
    // ==============================

    @Override
    @Transactional
    public List<DeliveryItemResponse> deliverOrder(Order order) {

        if (order.getOrderDetails() == null || order.getOrderDetails().isEmpty()) {
            throw new DeliveryException("Đơn hàng không có sản phẩm");
        }

        List<DeliveryItemResponse> results = new ArrayList<>();

        for (OrderDetail detail : order.getOrderDetails()) {

            List<DeliveryItemResponse> items =
                    deliverOrderDetail(detail, order.getUser().getId());

            results.addAll(items);
        }

        return results;
    }

    // ==============================
    // 2. Deliver từng OrderDetail
    // ==============================

    @Transactional
    public List<DeliveryItemResponse> deliverOrderDetail(OrderDetail detail,
                                                         Long userId) {

        Product product = detail.getProduct();
        Integer quantity = detail.getQuantity();

        if (quantity == null || quantity <= 0) {
            throw new DeliveryException("Số lượng không hợp lệ");
        }

        if (product.getType() == ProductType.KEY) {
            return deliverKeys(product, quantity, detail, userId);
        }

        if (product.getType() == ProductType.ACCOUNT) {
            return deliverAccounts(product, quantity, detail);
        }

        throw new DeliveryException("Loại sản phẩm không hỗ trợ");
    }

    // ==============================
    // 3. Deliver KEY (có thời hạn)
    // ==============================

    private List<DeliveryItemResponse> deliverKeys(Product product,
                                                   Integer quantity,
                                                   OrderDetail detail,
                                                   Long userId) {

        ProductPackage pkg = detail.getProductPackage();

        if (pkg == null) {
            throw new DeliveryException("Key phải có gói thời hạn");
        }

        List<DeliveryItemResponse> results = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {

            GameKey key = gameKeyRepository
                    .findFirstAvailableForUpdate(
                            product.getId(),
                            ItemStatus.AVAILABLE,
                            PageRequest.of(0, 1)
                    )
                    .stream()
                    .findFirst()
                    .orElseThrow(() ->
                            new OutOfStockException("Không đủ key trong kho"));

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiredAt = calculateExpiredAt(now, pkg);

            key.setStatus(ItemStatus.SOLD);
            key.setRentedByUserId(userId);
            key.setExpiredAt(expiredAt);
            key.setOrderDetail(detail);

            results.add(
                    new DeliveryItemResponse(
                            "KEY",
                            key.getLicenseKey(),
                            null,
                            "Hết hạn: " + expiredAt
                    )
            );
        }

        return results;
    }

    // ==============================
    // 4. Deliver ACCOUNT
    // ==============================

    private List<DeliveryItemResponse> deliverAccounts(Product product,
                                                       Integer quantity,
                                                       OrderDetail detail) {

        List<DeliveryItemResponse> results = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {

            GameAccount account = gameAccountRepository
                    .findFirstAvailableForUpdate(product.getId())
                    .orElseThrow(() ->
                            new OutOfStockException("Không đủ account"));

            account.setStatus(ItemStatus.SOLD);
            account.setOrderDetail(detail);

            gameAccountRepository.save(account);

            results.add(
                    new DeliveryItemResponse(
                            "ACCOUNT",
                            account.getUsername(),
                            account.getPassword(),
                            "Vui lòng đổi mật khẩu"
                    )
            );
        }

        return results;
    }

    // ==============================
    // 5. Tính thời hạn key
    // ==============================

    private LocalDateTime calculateExpiredAt(LocalDateTime now,
                                             ProductPackage pkg) {

        return switch (pkg.getDurationUnit()) {
            case HOUR -> now.plusHours(pkg.getDurationValue());
            case DAY -> now.plusDays(pkg.getDurationValue());
            case MONTH -> now.plusMonths(pkg.getDurationValue());
            default -> throw new IllegalArgumentException("DurationUnit không hợp lệ");
        };
    }
    // ==============================
    // 6. Kiểm tra tồn kho
    // ==============================

    @Override
    public Long getAvailableStock(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        if (product.getType() == ProductType.KEY) {
            return gameKeyRepository.countByProductIdAndStatus(
                    productId, ItemStatus.AVAILABLE);
        }

        if (product.getType() == ProductType.ACCOUNT) {
            return gameAccountRepository.countByProductIdAndStatus(
                    productId, ItemStatus.AVAILABLE);
        }

        return 0L;
    }
}




