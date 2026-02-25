package com.example.gameshopbackend.repository;

import com.example.gameshopbackend.entity.ProductPackage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductPackageRepository extends JpaRepository<ProductPackage, Long> {
}