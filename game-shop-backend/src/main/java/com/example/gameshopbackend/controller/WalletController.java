package com.example.gameshopbackend.controller;

import com.example.gameshopbackend.entity.WalletLog;
import com.example.gameshopbackend.security.UserPrincipal;
import com.example.gameshopbackend.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/balance")
    public Long balance(@AuthenticationPrincipal UserPrincipal user) {
        return walletService.getBalance(user.getId());
    }

    @GetMapping("/logs")
    public List<WalletLog> logs(@AuthenticationPrincipal UserPrincipal user) {
        return walletService.getLogs(user.getId());
    }

    @PostMapping("/topup")
    public void topup(@AuthenticationPrincipal UserPrincipal user,
                      @RequestBody com.example.gameshopbackend.dto.request.TopupRequest request) {
        walletService.topup(user.getId(), request.getAmount());
    }

    @PostMapping("/transfer")
    public void transfer(@AuthenticationPrincipal UserPrincipal user,
                         @RequestBody com.example.gameshopbackend.dto.request.TransferRequest request) {
        walletService.transfer(user.getId(), request.getToUserId(), request.getAmount());
    }
}
