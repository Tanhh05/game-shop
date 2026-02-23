package com.example.gameshopbackend.service;

import com.example.gameshopbackend.dto.response.DeliveryItemResponse;
import com.example.gameshopbackend.entity.Order;
import com.example.gameshopbackend.entity.OrderDetail;

import java.util.List;

/**
 * Service xử lý giao hàng tự động cho orders
 */
public interface DeliveryService {

    /**
     * Giao hàng toàn bộ chi tiết đơn hàng
     * @param order Order cần giao hàng
     * @return Danh sách các sản phẩm được giao
     */
    List<DeliveryItemResponse> deliverOrder(Order order);

    /**
     * Giao hàng một chi tiết đơn hàng
     * @param orderDetail Chi tiết cần giao hàng
     * @return Thông tin sản phẩm được giao
     */
    DeliveryItemResponse deliverOrderDetail(OrderDetail orderDetail);

    /**
     * Kiểm tra xem sản phẩm có thể giao hàng không
     * @param productId ID của sản phẩm
     * @param quantity Số lượng cần giao
     * @return true nếu còn hàng, false nếu hết
     */
    boolean hasStock(Long productId, Integer quantity);

    /**
     * Lấy số lượng hàng còn lại của sản phẩm
     */
    Long getAvailableStock(Long productId);
}

