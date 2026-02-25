package com.example.gameshopbackend.repository;

import com.example.gameshopbackend.entity.GameAccount;
import com.example.gameshopbackend.util.ItemStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GameAccountRepository extends JpaRepository<GameAccount, Long> {

    /**
     * Lấy tài khoản đầu tiên khả dụng của một sản phẩm
     */
    @Query("SELECT ga FROM GameAccount ga WHERE ga.product.id = :productId AND ga.status = 'AVAILABLE' ORDER BY ga.id ASC")
    List<GameAccount> findFirstAvailableByProductId(@Param("productId") Long productId, org.springframework.data.domain.Pageable pageable);

    /**
     * Đếm số lượng tài khoản khả dụng của một sản phẩm
     */
    Long countByProductIdAndStatus(Long productId, ItemStatus status);

    /**
     * Lấy tất cả tài khoản của một sản phẩm theo status
     */
    List<GameAccount> findByProductIdAndStatus(Long productId, ItemStatus status);

    /**
     * Lấy tất cả tài khoản của một sản phẩm
     */
    List<GameAccount> findByProductId(Long productId);

    /**
     * Kiểm tra tài khoản có tồn tại không
     */
    boolean existsByUsername(String username);

    List<GameAccount> findByOrderDetailId(Long orderDetailId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
       SELECT a FROM GameAccount a
       WHERE a.product.id = :productId
       AND a.status = 'AVAILABLE'
       ORDER BY a.id ASC
       """)
    Optional<GameAccount> findFirstAvailableForUpdate(Long productId);
}


