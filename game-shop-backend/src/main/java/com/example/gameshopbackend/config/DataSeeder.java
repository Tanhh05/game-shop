package com.example.gameshopbackend.config;

import com.example.gameshopbackend.entity.User;
import com.example.gameshopbackend.entity.Wallet;
import com.example.gameshopbackend.repository.UserRepository;
import com.example.gameshopbackend.repository.WalletRepository;
import com.example.gameshopbackend.util.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final WalletRepository walletRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (userRepo.existsByUsername("admin")) return;

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@gmail.com");
        admin.setPassword(
                passwordEncoder.encode("123456"));
        admin.setRole(Role.ADMIN);
        admin.setStatus(true);

        userRepo.save(admin);

        Wallet wallet = new Wallet();
        wallet.setUser(admin);
        wallet.setBalance(1_000_000L);

        walletRepo.save(wallet);

        System.out.println("✅ Seed admin thành công");
    }
}
