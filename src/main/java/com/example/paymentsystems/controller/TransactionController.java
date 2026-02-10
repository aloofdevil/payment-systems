package com.example.paymentsystems.controller;

import com.example.paymentsystems.dto.ApiResponse;
import com.example.paymentsystems.entity.Transaction;
import com.example.paymentsystems.entity.Wallet;
import com.example.paymentsystems.service.WalletService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final WalletService walletService;

    public TransactionController(WalletService walletService) {
        this.walletService = walletService;
    }

    // ✅ DEPOSIT
    @PostMapping("/{userId}/deposit")
    public ApiResponse deposit(
            @PathVariable Long userId,
            @RequestParam Double amount) {

        Wallet wallet = walletService.deposit(userId, amount);
        return new ApiResponse(true, "Deposit successful", wallet);
    }

    @PostMapping("/{userId}/withdraw")
    public ApiResponse withdraw(
            @PathVariable Long userId,
            @RequestParam Double amount) {

        Wallet wallet = walletService.withdraw(userId, amount);
        return new ApiResponse(true, "Withdrawal successful", wallet);
    }

    @PostMapping("/{fromUser}/transfer/{toUser}")
    public ApiResponse transfer(
            @PathVariable Long fromUser,
            @PathVariable Long toUser,
            @RequestParam Double amount) {

        walletService.transfer(fromUser, toUser, amount);
        return new ApiResponse(true,
                "Transfer successful from " + fromUser + " to " + toUser,
                null);
    }

    @GetMapping("/user/{userId}")
    public ApiResponse getTransactions(@PathVariable Long userId) {
        return new ApiResponse(true,
                "Transactions fetched",
                walletService.getTransactionsByUser(userId));
    }



}
