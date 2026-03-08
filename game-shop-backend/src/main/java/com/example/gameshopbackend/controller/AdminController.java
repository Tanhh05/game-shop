package com.example.gameshopbackend.controller;

import com.example.gameshopbackend.dto.request.BulkImportAccountsRequest;
import com.example.gameshopbackend.dto.request.BulkImportKeysRequest;
import com.example.gameshopbackend.dto.response.AdminOrderResponse;
import com.example.gameshopbackend.dto.response.InventoryStatsResponse;
import com.example.gameshopbackend.dto.response.UserResponse;
import com.example.gameshopbackend.entity.Order;
import com.example.gameshopbackend.entity.User;
import com.example.gameshopbackend.mapper.UserMapper;
import com.example.gameshopbackend.repository.OrderRepository;
import com.example.gameshopbackend.repository.UserRepository;
import com.example.gameshopbackend.service.InventoryService;
import com.example.gameshopbackend.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final WalletService walletService;
    private final UserMapper userMapper;

    // ============ USER MANAGEMENT ============

    /**
     * Lấy danh sách tất cả users
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> users = userRepository.findAll(pageable);
            Page<UserResponse> response = users.map(userMapper::toResponse);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lấy danh sách users thất bại", "detail", ex.getMessage()));
        }
    }

    /**
     * Lấy chi tiết user
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserDetail(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));
            UserResponse response = userMapper.toResponse(user);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lấy chi tiết user thất bại", "detail", ex.getMessage()));
        }
    }

    /**
     * Khóa/mở tài khoản user
     */
    @PatchMapping("/users/{id}/status")
    public ResponseEntity<?> changeUserStatus(
            @PathVariable Long id,
            @RequestParam Boolean status
    ) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));
            user.setStatus(status);
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "Cập nhật trạng thái user thành công", "userId", id, "status", status));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Cập nhật trạng thái user thất bại", "detail", ex.getMessage()));
        }
    }

    /**
     * Lấy lịch sử mua hàng của user
     */
    @GetMapping("/users/{id}/orders")
    public ResponseEntity<?> getUserOrders(@PathVariable Long id) {
        try {
            if (!userRepository.existsById(id)) {
                throw new IllegalArgumentException("User không tồn tại");
            }
            List<Order> orders = orderRepository.findByUserId(id);
            List<AdminOrderResponse> response = orders.stream()
                    .map(order -> new AdminOrderResponse(
                            order.getId(),
                            order.getUser().getId(),
                            order.getUser().getUsername(),
                            order.getTotalAmount(),
                            order.getStatus().toString(),
                            order.getOrderDetails().size(),
                            order.getCreatedAt()
                    ))
                    .toList();
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lấy lịch sử mua hàng thất bại", "detail", ex.getMessage()));
        }
    }

    /**
     * Xem ví & giao dịch của user
     */
    @GetMapping("/users/{id}/wallet")
    public ResponseEntity<?> getUserWallet(@PathVariable Long id) {
        try {
            if (!userRepository.existsById(id)) {
                throw new IllegalArgumentException("User không tồn tại");
            }
            Long balance = walletService.getBalance(id);
            return ResponseEntity.ok(Map.of("userId", id, "balance", balance));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lấy thông tin ví thất bại", "detail", ex.getMessage()));
        }
    }

    // ============ ORDER MANAGEMENT ============

    /**
     * Lấy danh sách đơn hàng
     */
    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Order> orders = orderRepository.findAll(pageable);
            Page<AdminOrderResponse> response = orders.map(order ->
                    new AdminOrderResponse(
                            order.getId(),
                            order.getUser().getId(),
                            order.getUser().getUsername(),
                            order.getTotalAmount(),
                            order.getStatus().toString(),
                            order.getOrderDetails().size(),
                            order.getCreatedAt()
                    )
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lấy danh sách đơn hàng thất bại", "detail", ex.getMessage()));
        }
    }

    /**
     * Lấy chi tiết đơn hàng
     */
    @GetMapping("/orders/{id}")
    public ResponseEntity<?> getOrderDetail(@PathVariable Long id) {
        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));
            AdminOrderResponse response = new AdminOrderResponse(
                    order.getId(),
                    order.getUser().getId(),
                    order.getUser().getUsername(),
                    order.getTotalAmount(),
                    order.getStatus().toString(),
                    order.getOrderDetails().size(),
                    order.getCreatedAt()
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lấy chi tiết đơn hàng thất bại", "detail", ex.getMessage()));
        }
    }

    // ============ INVENTORY MANAGEMENT ============

    /**
     * Nhập keys hàng loạt
     */
    @PostMapping("/inventory/import-keys")
    public ResponseEntity<?> importKeys(@Valid @RequestBody BulkImportKeysRequest request) {
        try {
            Integer importedCount = inventoryService.importKeys(request);
            return ResponseEntity.ok(Map.of("message", "Import keys thành công", "count", importedCount));
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
    @PostMapping("/inventory/import-accounts")
    public ResponseEntity<?> importAccounts(@Valid @RequestBody BulkImportAccountsRequest request) {
        try {
            Integer importedCount = inventoryService.importAccounts(request);
            return ResponseEntity.ok(Map.of("message", "Import accounts thành công", "count", importedCount));
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
    @GetMapping("/inventory/stats/{productId}")
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
    @GetMapping("/inventory/stats")
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
    @DeleteMapping("/inventory/keys/{keyId}")
    public ResponseEntity<?> deleteKey(@PathVariable Long keyId) {
        try {
            inventoryService.deleteKey(keyId);
            return ResponseEntity.ok(Map.of("message", "Xóa key thành công"));
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
    @DeleteMapping("/inventory/accounts/{accountId}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long accountId) {
        try {
            inventoryService.deleteAccount(accountId);
            return ResponseEntity.ok(Map.of("message", "Xóa account thành công"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Xóa account thất bại", "detail", ex.getMessage()));
        }
    }
}
