package com.example.gameshopbackend.service;

import com.example.gameshopbackend.dto.request.LoginRequest;
import com.example.gameshopbackend.dto.request.RegisterRequest;
import com.example.gameshopbackend.dto.response.LoginResponse;
import com.example.gameshopbackend.entity.User;
import com.example.gameshopbackend.entity.Wallet;
import com.example.gameshopbackend.exception.BadRequestException;
import com.example.gameshopbackend.jwt.JwtService;
import com.example.gameshopbackend.repository.UserRepository;
import com.example.gameshopbackend.repository.WalletRepository;
import com.example.gameshopbackend.util.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final WalletRepository walletRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // REGISTER
    @Transactional
    public LoginResponse register(RegisterRequest request) {

        if (userRepo.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username đã tồn tại");
        }

        if (userRepo.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email đã tồn tại");
        }

        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setEmail(request.getEmail().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setStatus(true);
        user.setCreatedAt(LocalDateTime.now());

        // 🔥 Generate depositCode an toàn (không trùng)
        String depositCode;
        do {
            depositCode = "NAP" + UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 8)
                    .toUpperCase();
        } while (userRepo.existsByDepositCode(depositCode));

        user.setDepositCode(depositCode);

        userRepo.save(user);

        // 🔥 Tạo ví cho user
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(0L);
        wallet.setUpdatedAt(LocalDateTime.now());

        walletRepo.save(wallet);

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                wallet.getBalance()
        );
    }

    // LOGIN
    public LoginResponse login(LoginRequest request) {

        User user = userRepo
                .findByUsernameOrEmail(
                        request.getUsername(),
                        request.getUsername()
                )
                .orElseThrow(() ->
                        new BadRequestException("Tài khoản không tồn tại"));

        if (!user.getStatus()) {
            throw new BadRequestException("Tài khoản bị khóa");
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {
            throw new BadRequestException("Sai mật khẩu");
        }

        Wallet wallet = walletRepo
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new BadRequestException("Ví không tồn tại"));

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                wallet.getBalance()
        );
    }
}
