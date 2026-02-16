package com.example.paymentsystems.controller;

import com.example.paymentsystems.dto.ApiResponse;
import com.example.paymentsystems.dto.TransferRequest;
import com.example.paymentsystems.entity.Wallet;
import com.example.paymentsystems.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final WalletService walletService;

    public TransactionController(WalletService walletService) {
        this.walletService = walletService;
    }

    // ==============================
    // ✅ DEPOSIT
    // ==============================
    @PostMapping("/{userId}/deposit")
    public ApiResponse deposit(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount) {

        Wallet wallet = walletService.deposit(userId, amount);

        return new ApiResponse(true, "Deposit successful", wallet);
    }

    // ==============================
    // ✅ WITHDRAW
    // ==============================
    @PostMapping("/{userId}/withdraw")
    public ApiResponse withdraw(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount) {

        Wallet wallet = walletService.withdraw(userId, amount);

        return new ApiResponse(true, "Withdrawal successful", wallet);
    }

    // ==============================
    // ✅ TRANSFER
    // ==============================
    @PostMapping("/transfer")
    public ApiResponse transfer(@Valid @RequestBody TransferRequest request) {

        walletService.transfer(
                request.getFromUser(),
                request.getToUser(),
                request.getAmount()
        );

        return new ApiResponse(true, "Transfer successful", null);
    }

    // ==============================
    // ✅ GET USER TRANSACTIONS (With Pagination)
    // ==============================
    @GetMapping("/user/{userId}")
    public ApiResponse getTransactions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Page<?> transactions =
                walletService.getTransactionsByUser(userId, page, size);

        return new ApiResponse(
                true,
                "Transactions fetched",
                transactions
        );
    }
}
