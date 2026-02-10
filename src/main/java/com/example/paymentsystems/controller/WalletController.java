package com.example.paymentsystems.controller;

import com.example.paymentsystems.entity.Wallet;
import com.example.paymentsystems.service.WalletService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    // ✅ CREATE WALLET (idempotent)
    @PostMapping("/{userId}")
    public Wallet createWallet(@PathVariable Long userId) {
        return walletService.createWallet(userId);
    }

    // ✅ GET WALLET
    @GetMapping("/{userId}")
    public Wallet getWallet(@PathVariable Long userId) {
        return walletService.getWalletByUserId(userId);
    }
}

