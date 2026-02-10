package com.example.gameshopbackend.repository;

import com.example.gameshopbackend.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}