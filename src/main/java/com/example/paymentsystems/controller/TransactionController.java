package com.example.paymentsystems.controller;

import com.example.paymentsystems.entity.Transaction;
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

    @GetMapping("/user/{userId}")
    public List<Transaction> getUserTransactions(@PathVariable Long userId) {
        return walletService.getTransactionsByUser(userId);
    }
}
