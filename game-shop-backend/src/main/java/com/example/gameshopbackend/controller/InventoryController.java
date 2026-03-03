package com.example.gameshopbackend.controller;

import com.example.gameshopbackend.dto.request.BulkImportAccountsRequest;
import com.example.gameshopbackend.dto.request.BulkImportKeysRequest;
import com.example.gameshopbackend.dto.response.InventoryStatsResponse;
import com.example.gameshopbackend.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Nhập keys hàng loạt
     */
    @PostMapping("/keys/import")
    public ResponseEntity<?> importKeys(@Valid @RequestBody BulkImportKeysRequest request) {
        try {
            Integer importedCount = inventoryService.importKeys(request);
            return ResponseEntity.ok(Map.of(
                    "message", "Import keys thành công",
                    "count", importedCount,
                    "productId", request.getProductId()
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Import keys thất bại", "detail", ex.getMessage()));
        }
    }

    /**
     * Nhập accounts hàng loạt
     */
    @PostMapping("/accounts/import")
    public ResponseEntity<?> importAccounts(@Valid @RequestBody BulkImportAccountsRequest request) {
        try {
            Integer importedCount = inventoryService.importAccounts(request);
            return ResponseEntity.ok(Map.of(
                    "message", "Import accounts thành công",
                    "count", importedCount,
                    "productId", request.getProductId()
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Import accounts thất bại", "detail", ex.getMessage()));
        }
    }

    /**
     * Lấy thống kê kho của một sản phẩm
     */
    @GetMapping("/stats/{productId}")
    public ResponseEntity<?> getInventoryStats(@PathVariable Long productId) {
        try {
            InventoryStatsResponse stats = inventoryService.getInventoryStats(productId);
            return ResponseEntity.ok(stats);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lấy thống kê kho thất bại", "detail", ex.getMessage()));
        }
    }

    /**
     * Lấy thống kê kho toàn bộ sản phẩm
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getAllInventoryStats() {
        try {
            List<InventoryStatsResponse> stats = inventoryService.getAllInventoryStats();
            return ResponseEntity.ok(stats);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lấy thống kê kho thất bại", "detail", ex.getMessage()));
        }
    }

    /**
     * Xóa key từ kho
     */
    @DeleteMapping("/keys/{keyId}")
    public ResponseEntity<?> deleteKey(@PathVariable Long keyId) {
        try {
            inventoryService.deleteKey(keyId);
            return ResponseEntity.ok(Map.of("message", "Xóa key thành công", "keyId", keyId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Xóa key thất bại", "detail", ex.getMessage()));
        }
    }

    /**
     * Xóa account từ kho
     */
    @DeleteMapping("/accounts/{accountId}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long accountId) {
        try {
            inventoryService.deleteAccount(accountId);
            return ResponseEntity.ok(Map.of("message", "Xóa account thành công", "accountId", accountId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Xóa account thất bại", "detail", ex.getMessage()));
        }
    }
}
