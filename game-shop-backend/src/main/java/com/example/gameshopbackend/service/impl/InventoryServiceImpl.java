package com.example.gameshopbackend.service.impl;

import com.example.gameshopbackend.dto.request.BulkImportAccountsRequest;
import com.example.gameshopbackend.dto.request.BulkImportKeysRequest;
import com.example.gameshopbackend.dto.response.AdminInventoryAccountResponse;
import com.example.gameshopbackend.dto.response.AdminInventoryKeyResponse;
import com.example.gameshopbackend.dto.response.InventoryStatsResponse;
import com.example.gameshopbackend.entity.GameAccount;
import com.example.gameshopbackend.entity.GameKey;
import com.example.gameshopbackend.entity.Product;
import com.example.gameshopbackend.repository.GameAccountRepository;
import com.example.gameshopbackend.repository.GameKeyRepository;
import com.example.gameshopbackend.repository.ProductRepository;
import com.example.gameshopbackend.service.InventoryService;
import com.example.gameshopbackend.util.ItemStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final GameKeyRepository gameKeyRepository;
    private final GameAccountRepository gameAccountRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Integer importKeys(BulkImportKeysRequest request) {
        if (request.getKeys() == null || request.getKeys().isEmpty()) {
            throw new IllegalArgumentException("Danh sách keys không được để trống");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));

        int count = 0;
        for (String keyValue : request.getKeys()) {
            if (keyValue != null && !keyValue.isBlank()) {
                // Kiểm tra key đã tồn tại
                if (!gameKeyRepository.existsByLicenseKey(keyValue)) {
                    GameKey gameKey = new GameKey();
                    gameKey.setProduct(product);
                    gameKey.setLicenseKey(keyValue);
                    gameKey.setStatus(ItemStatus.AVAILABLE);
                    gameKeyRepository.save(gameKey);
                    count++;
                }
            }
        }

        return count;
    }

    @Override
    @Transactional
    public Integer importAccounts(BulkImportAccountsRequest request) {
        if (request.getAccounts() == null || request.getAccounts().isEmpty()) {
            throw new IllegalArgumentException("Danh sách accounts không được để trống");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));

        int count = 0;
        for (BulkImportAccountsRequest.Account acc : request.getAccounts()) {
            if (acc.getUsername() != null && !acc.getUsername().isBlank() &&
                    acc.getPassword() != null && !acc.getPassword().isBlank()) {

                // Kiểm tra account đã tồn tại
                if (!gameAccountRepository.existsByUsername(acc.getUsername())) {
                    GameAccount gameAccount = new GameAccount();
                    gameAccount.setProduct(product);
                    gameAccount.setUsername(acc.getUsername());
                    gameAccount.setPassword(acc.getPassword());
                    gameAccount.setStatus(ItemStatus.AVAILABLE);
                    gameAccountRepository.save(gameAccount);
                    count++;
                }
            }
        }

        return count;
    }

    @Override
    public InventoryStatsResponse getInventoryStats(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));

        Long availableKeys = gameKeyRepository.countByProductIdAndStatus(productId, ItemStatus.AVAILABLE);
        Long soldKeys = gameKeyRepository.countByProductIdAndStatus(productId, ItemStatus.SOLD);
        Long availableAccounts = gameAccountRepository.countByProductIdAndStatus(productId, ItemStatus.AVAILABLE);
        Long soldAccounts = gameAccountRepository.countByProductIdAndStatus(productId, ItemStatus.SOLD);

        return new InventoryStatsResponse(
                productId,
                product.getTitle(),
                availableKeys,
                soldKeys,
                availableAccounts,
                soldAccounts,
                availableKeys + soldKeys + availableAccounts + soldAccounts
        );
    }

    @Override
    public List<InventoryStatsResponse> getAllInventoryStats() {
        return productRepository.findAll().stream()
                .map(product -> getInventoryStats(product.getId()))
                .toList();
    }

    @Override
    @Transactional
    public void deleteKey(Long keyId) {
        GameKey gameKey = gameKeyRepository.findById(keyId)
                .orElseThrow(() -> new IllegalArgumentException("Key không tồn tại"));

        // Chỉ xóa key chưa bán
        if (gameKey.getStatus() != ItemStatus.AVAILABLE) {
            throw new IllegalArgumentException("Chỉ có thể xóa key chưa bán");
        }

        gameKeyRepository.deleteById(keyId);
    }

    @Override
    @Transactional
    public void deleteAccount(Long accountId) {
        GameAccount gameAccount = gameAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account không tồn tại"));

        // Chỉ xóa account chưa bán
        if (gameAccount.getStatus() != ItemStatus.AVAILABLE) {
            throw new IllegalArgumentException("Chỉ có thể xóa account chưa bán");
        }

        gameAccountRepository.deleteById(accountId);
    }

    @Override
    public Page<AdminInventoryKeyResponse> getKeysByProduct(Long productId, int page, int size) {
        return null;
    }

    @Override
    public Page<AdminInventoryAccountResponse> getAccountsByProduct(Long productId, int page, int size) {
        return null;
    }
}

