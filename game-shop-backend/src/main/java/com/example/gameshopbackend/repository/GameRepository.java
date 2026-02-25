package com.example.gameshopbackend.repository;

import com.example.gameshopbackend.entity.Game;
import com.example.gameshopbackend.entity.GameAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findBySlug(String slug);

    Page<Game> findByStatus(Boolean status, Pageable pageable);

}

