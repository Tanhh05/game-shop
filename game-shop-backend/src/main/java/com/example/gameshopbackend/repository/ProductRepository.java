package com.example.gameshopbackend.repository;

import com.example.gameshopbackend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStatusTrue();

    List<Product> findByGame_IdAndStatusTrue(Long gameId);

    Optional<Product> findBySlugAndStatusTrue(String slug);

    Page<Product> findByStatusTrue(Pageable pageable);

    @Query("""
       SELECT p FROM Product p
       LEFT JOIN FETCH p.packages
       WHERE p.id = :id
       """)
    Optional<Product> findByIdWithPackages(Long id);
}
