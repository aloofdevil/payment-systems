package com.example.paymentsystems.controller;

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
    public Wallet deposit(
            @PathVariable Long userId,
            @RequestParam Double amount) {

        return walletService.deposit(userId, amount);
    }

    // ✅ WITHDRAW
    @PostMapping("/{userId}/withdraw")
    public Wallet withdraw(
            @PathVariable Long userId,
            @RequestParam Double amount) {

        return walletService.withdraw(userId, amount);
    }

    // ✅ TRANSFER
    @PostMapping("/{fromUser}/transfer/{toUser}")
    public Wallet transfer(
            @PathVariable Long fromUser,
            @PathVariable Long toUser,
            @RequestParam Double amount) {

        return walletService.transfer(fromUser, toUser, amount);
    }

    // ✅ GET TRANSACTIONS (THIS IS WHAT YOU WANT)
    @GetMapping("/user/{userId}")
    public List<Transaction> getTransactions(@PathVariable Long userId) {
        return walletService.getTransactionsByUser(userId);
    }


}
