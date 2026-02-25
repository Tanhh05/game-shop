package com.example.gameshopbackend.repository;

import com.example.gameshopbackend.entity.GameKey;
import com.example.gameshopbackend.util.ItemStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GameKeyRepository extends JpaRepository<GameKey, Long> {

    /**
     * Lấy danh sách các key còn khả dụng của một sản phẩm
     */
    @Query("SELECT gk FROM GameKey gk WHERE gk.product.id = :productId AND gk.status = 'AVAILABLE' ORDER BY gk.id ASC")
    List<GameKey> findFirstAvailableByProductId(@Param("productId") Long productId, org.springframework.data.domain.Pageable pageable);

    /**
     * Đếm số lượng key còn khả dụng của một sản phẩm
     */
    Long countByProductIdAndStatus(Long productId, ItemStatus status);

    /**
     * Lấy tất cả các key của một sản phẩm theo status
     */
    List<GameKey> findByProductIdAndStatus(Long productId, ItemStatus status);

    /**
     * Lấy tất cả các key của một sản phẩm
     */
    List<GameKey> findByProductId(Long productId);

    /**
     * Kiểm tra key có tồn tại không
     */
    boolean existsByLicenseKey(String licenseKey);

    List<GameKey> findByOrderDetailId(Long orderDetailId);

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("""
//       SELECT k FROM GameKey k
//       WHERE k.product.id = :productId
//       AND k.status = 'AVAILABLE'
//       ORDER BY k.id ASC
//       """)
//    Optional<GameKey> findFirstAvailableForUpdate(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
   SELECT k FROM GameKey k
   WHERE k.product.id = :productId
   AND k.status = :status
   ORDER BY k.id ASC
""")
    List<GameKey> findFirstAvailableForUpdate(
            @Param("productId") Long productId,
            @Param("status") ItemStatus status,
            Pageable pageable
    );
}


