package com.example.gameshopbackend.service;

import com.example.gameshopbackend.dto.request.BulkImportAccountsRequest;
import com.example.gameshopbackend.dto.request.BulkImportKeysRequest;
import com.example.gameshopbackend.dto.response.AdminInventoryAccountResponse;
import com.example.gameshopbackend.dto.response.AdminInventoryKeyResponse;
import com.example.gameshopbackend.dto.response.InventoryStatsResponse;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service quản lý kho hàng (inventory)
 */
public interface InventoryService {

    /**
     * Nhập key hàng loạt
     * @param request Danh sách keys cần import
     * @return Số lượng keys được import thành công
     */
    Integer importKeys(BulkImportKeysRequest request);

    /**
     * Nhập account hàng loạt
     * @param request Danh sách accounts cần import
     * @return Số lượng accounts được import thành công
     */
    Integer importAccounts(BulkImportAccountsRequest request);

    /**
     * Lấy thống kê kho hàng của một sản phẩm
     */
    InventoryStatsResponse getInventoryStats(Long productId);

    /**
     * Lấy thống kê kho hàng của tất cả sản phẩm
     */
    List<InventoryStatsResponse> getAllInventoryStats();

    /**
     * Xóa key từ kho
     */
    void deleteKey(Long keyId);

    /**
     * Xóa account từ kho
     */
    void deleteAccount(Long accountId);

    /**
     * Lấy danh sách key của 1 sản phẩm (phân trang)
     */
    Page<AdminInventoryKeyResponse> getKeysByProduct(Long productId, int page, int size);

    /**
     * Lấy danh sách account của 1 sản phẩm (phân trang)
     */
    Page<AdminInventoryAccountResponse> getAccountsByProduct(Long productId, int page, int size);
}
