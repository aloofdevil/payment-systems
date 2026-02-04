package com.example.paymentsystems.service;

import com.example.paymentsystems.entity.Transaction;
import com.example.paymentsystems.entity.TransactionType;
import com.example.paymentsystems.entity.Wallet;
import com.example.paymentsystems.repository.TransactionRepository;
import com.example.paymentsystems.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WalletService(WalletRepository walletRepository,
                         TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public Wallet createWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Wallet wallet = new Wallet(userId);
                    return walletRepository.save(wallet);
                });
    }

    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    @Transactional
    public Wallet deposit(Long userId, Double amount) {

        if (amount == null || amount <= 0) {
            throw new RuntimeException("Deposit amount must be greater than zero");
        }

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        // Update balance
        wallet.setBalance(wallet.getBalance() + amount);
        walletRepository.save(wallet);

        // Create transaction record (ledger)
        Transaction txn = new Transaction();
        txn.setUserId(userId);
        txn.setAmount(amount);
        txn.setType(TransactionType.DEPOSIT);

        transactionRepository.save(txn);

        return wallet;
    }
    @Transactional
    public Wallet withdraw(Long userId, Double amount) {

        if (amount == null || amount <= 0) {
            throw new RuntimeException("Withdraw amount must be greater than zero");
        }

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        // Deduct balance
        wallet.setBalance(wallet.getBalance() - amount);
        walletRepository.save(wallet);

        // Create transaction record
        Transaction txn = new Transaction();
        txn.setUserId(userId);
        txn.setAmount(amount);
        txn.setType(TransactionType.WITHDRAW);

        transactionRepository.save(txn);

        return wallet;
    }

}
