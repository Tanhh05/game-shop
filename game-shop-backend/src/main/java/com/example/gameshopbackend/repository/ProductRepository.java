package com.example.gameshopbackend.repository;

import com.example.gameshopbackend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStatusTrue();

    List<Product> findByGame_IdAndStatusTrue(Long gameId);

    Optional<Product> findBySlugAndStatusTrue(String slug);
}
