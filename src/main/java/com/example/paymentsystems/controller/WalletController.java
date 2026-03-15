package com.example.paymentsystems.controller;

import com.example.paymentsystems.dto.ApiResponse;
import com.example.paymentsystems.service.WalletService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    // ==============================
    // CREATE WALLET
    // ==============================
    @PostMapping("/{userId}")
    public ApiResponse createWallet(@PathVariable Long userId) {

        return new ApiResponse(
                true,
                "Wallet created",
                walletService.createWallet(userId)
        );
    }

    // ==============================
    // GET WALLET
    // ==============================
    @GetMapping("/{userId}")
    public ApiResponse getWallet(@PathVariable Long userId) {

        return new ApiResponse(
                true,
                "Wallet fetched",
                walletService.getWalletByUserId(userId)
        );
    }

    // ==============================
    // WALLET SUMMARY
    // ==============================
    @GetMapping("/{userId}/summary")
    public ApiResponse getSummary(@PathVariable Long userId) {

        return new ApiResponse(
                true,
                "Wallet summary fetched",
                walletService.getWalletSummary(userId)
        );
    }
}