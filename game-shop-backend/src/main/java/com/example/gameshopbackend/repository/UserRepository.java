package com.example.gameshopbackend.repository;

import com.example.gameshopbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsernameOrEmail(String username, String email);

  Optional<User> findByUsername(String username);

  boolean existsByUsername(String username);


}