package com.example.paymentsystems.service;

import com.example.paymentsystems.entity.Wallet;
import com.example.paymentsystems.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet createWallet(Long userId) {
        Wallet wallet = new Wallet(userId);
        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet credit(Long userId, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.credit(amount);
        return wallet;
    }

    @Transactional
    public Wallet debit(Long userId, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.debit(amount);
        return wallet;
    }
}
