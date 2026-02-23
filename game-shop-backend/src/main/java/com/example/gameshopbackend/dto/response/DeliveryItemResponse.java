package com.example.gameshopbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DeliveryItemResponse {
    /**
     * Loại sản phẩm (KEY hoặc ACCOUNT)
     */
    private String type;

    /**
     * Thông tin giao hàng - có thể là key hoặc username
     */
    private String deliveryKey;

    /**
     * Cho ACCOUNT - password
     */
    private String deliveryValue;

    /**
     * Ghi chú thêm
     */
    private String note;
}

