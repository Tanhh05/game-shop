package com.example.gameshopbackend.service.impl;

import com.example.gameshopbackend.dto.response.DeliveryItemResponse;
import com.example.gameshopbackend.entity.GameAccount;
import com.example.gameshopbackend.entity.GameKey;
import com.example.gameshopbackend.entity.Order;
import com.example.gameshopbackend.entity.OrderDetail;
import com.example.gameshopbackend.entity.Product;
import com.example.gameshopbackend.exception.DeliveryException;
import com.example.gameshopbackend.exception.OutOfStockException;
import com.example.gameshopbackend.repository.GameAccountRepository;
import com.example.gameshopbackend.repository.GameKeyRepository;
import com.example.gameshopbackend.service.DeliveryService;
import com.example.gameshopbackend.util.ItemStatus;
import com.example.gameshopbackend.util.ProductType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final GameKeyRepository gameKeyRepository;
    private final GameAccountRepository gameAccountRepository;

    @Override
    @Transactional
    public List<DeliveryItemResponse> deliverOrder(Order order) {
        List<DeliveryItemResponse> deliveredItems = new ArrayList<>();

        if (order.getOrderDetails() == null || order.getOrderDetails().isEmpty()) {
            throw new DeliveryException("Đơn hàng không có chi tiết");
        }

        for (OrderDetail detail : order.getOrderDetails()) {
            try {
                DeliveryItemResponse item = deliverOrderDetail(detail);
                deliveredItems.add(item);
            } catch (OutOfStockException ex) {
                throw new DeliveryException("Sản phẩm " + detail.getProduct().getTitle() + " hết hàng. Đơn hàng thất bại.");
            }
        }

        return deliveredItems;
    }

    @Override
    @Transactional
    public DeliveryItemResponse deliverOrderDetail(OrderDetail orderDetail) {
        Product product = orderDetail.getProduct();
        Integer quantity = orderDetail.getQuantity();

        if (quantity == null || quantity <= 0) {
            throw new DeliveryException("Số lượng sản phẩm không hợp lệ");
        }

        // Kiểm tra loại sản phẩm
        if (product.getType() == ProductType.KEY) {
            return deliverKey(product, quantity);
        } else if (product.getType() == ProductType.ACCOUNT) {
            return deliverAccount(product, quantity);
        } else {
            throw new DeliveryException("Loại sản phẩm không hỗ trợ giao hàng tự động: " + product.getType());
        }
    }

    /**
     * Giao hàng KEY
     */
    private DeliveryItemResponse deliverKey(Product product, Integer quantity) {
        if (quantity > 1) {
            throw new DeliveryException("Chỉ có thể mua 1 key tại một thời điểm");
        }

        List<GameKey> availableKeys = gameKeyRepository.findFirstAvailableByProductId(product.getId(), PageRequest.of(0, 1));
        if (availableKeys.isEmpty()) {
            throw new OutOfStockException("Không có key nào khả dụng cho " + product.getTitle());
        }

        GameKey availableKey = availableKeys.get(0);

        // Đánh dấu key đã bán
        availableKey.setStatus(ItemStatus.SOLD);
        gameKeyRepository.save(availableKey);

        return new DeliveryItemResponse(
                "KEY",
                availableKey.getLicenseKey(),
                null,
                "Key sẽ hết hạn trong 7 ngày. Vui lòng kích hoạt sớm nhất có thể."
        );
    }

    /**
     * Giao hàng ACCOUNT
     */
    private DeliveryItemResponse deliverAccount(Product product, Integer quantity) {
        if (quantity > 1) {
            throw new DeliveryException("Chỉ có thể mua 1 account tại một thời điểm");
        }

        List<GameAccount> availableAccounts = gameAccountRepository.findFirstAvailableByProductId(product.getId(), PageRequest.of(0, 1));
        if (availableAccounts.isEmpty()) {
            throw new OutOfStockException("Không có account nào khả dụng cho " + product.getTitle());
        }

        GameAccount availableAccount = availableAccounts.get(0);

        // Đánh dấu account đã bán
        availableAccount.setStatus(ItemStatus.SOLD);
        gameAccountRepository.save(availableAccount);

        return new DeliveryItemResponse(
                "ACCOUNT",
                availableAccount.getUsername(),
                availableAccount.getPassword(),
                "Vui lòng đổi mật khẩu sau khi đăng nhập lần đầu tiên."
        );
    }

    @Override
    public boolean hasStock(Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            return false;
        }
        Long available = getAvailableStock(productId);
        return available >= quantity;
    }

    @Override
    public Long getAvailableStock(Long productId) {
        // Mục đích demo: trả về số lượng key/account khả dụng
        // Trong production có thể cần kết hợp cả KEY và ACCOUNT
        Long keyCount = gameKeyRepository.countByProductIdAndStatus(productId, ItemStatus.AVAILABLE);
        Long accountCount = gameAccountRepository.countByProductIdAndStatus(productId, ItemStatus.AVAILABLE);
        return Math.max(keyCount, accountCount);
    }
}




