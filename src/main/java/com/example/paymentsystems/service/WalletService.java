package com.example.paymentsystems.service;

import com.example.paymentsystems.dto.WalletSummaryResponse;
import com.example.paymentsystems.entity.Transaction;
import com.example.paymentsystems.entity.TransactionType;
import com.example.paymentsystems.entity.Wallet;
import com.example.paymentsystems.repository.TransactionRepository;
import com.example.paymentsystems.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WalletService(WalletRepository walletRepository,
                         TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    // ==============================
    // ✅ CREATE WALLET
    // ==============================
    public Wallet createWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> walletRepository.save(new Wallet(userId)));
    }

    // ==============================
    // ✅ GET WALLET
    // ==============================
    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    // ==============================
    // ✅ DEPOSIT
    // ==============================
    @Transactional
    public Wallet deposit(Long userId, BigDecimal amount) {

        validateAmount(amount);

        Wallet wallet = getWalletByUserId(userId);

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        Transaction txn =
                new Transaction(userId, amount, TransactionType.DEPOSIT);

        transactionRepository.save(txn);

        return wallet;
    }

    // ==============================
    // ✅ WITHDRAW
    // ==============================
    @Transactional
    public Wallet withdraw(Long userId, BigDecimal amount) {

        validateAmount(amount);

        Wallet wallet = getWalletByUserId(userId);

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        Transaction txn =
                new Transaction(userId, amount, TransactionType.WITHDRAW);

        transactionRepository.save(txn);

        return wallet;
    }

    // ==============================
    // ✅ TRANSFER
    // ==============================
    @Transactional
    public Wallet transfer(Long fromUser, Long toUser, BigDecimal amount) {

        validateAmount(amount);

        Wallet sender = getWalletByUserId(fromUser);
        Wallet receiver = getWalletByUserId(toUser);

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Deduct from sender
        sender.setBalance(sender.getBalance().subtract(amount));
        walletRepository.save(sender);

        // Credit receiver
        receiver.setBalance(receiver.getBalance().add(amount));
        walletRepository.save(receiver);

        // Record transactions
        Transaction debitTxn =
                new Transaction(fromUser, amount, TransactionType.TRANSFER_OUT);

        Transaction creditTxn =
                new Transaction(toUser, amount, TransactionType.TRANSFER_IN);

        transactionRepository.save(debitTxn);
        transactionRepository.save(creditTxn);

        return sender;
    }

    // ==============================
    // ✅ GET TRANSACTIONS (Pagination)
    // ==============================
    public Page<Transaction> getTransactionsByUser(
            Long userId,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return transactionRepository.findByUserId(userId, pageable);
    }

    // ==============================
    // ✅ WALLET SUMMARY
    // ==============================
    public WalletSummaryResponse getWalletSummary(Long userId) {

        Wallet wallet = getWalletByUserId(userId);

        List<Transaction> transactions =
                transactionRepository.findByUserId(userId, Pageable.unpaged())
                        .getContent();

        BigDecimal totalDeposits = BigDecimal.ZERO;
        BigDecimal totalWithdrawals = BigDecimal.ZERO;
        BigDecimal totalTransfersIn = BigDecimal.ZERO;
        BigDecimal totalTransfersOut = BigDecimal.ZERO;

        for (Transaction txn : transactions) {

            switch (txn.getType()) {
                case DEPOSIT ->
                        totalDeposits = totalDeposits.add(txn.getAmount());

                case WITHDRAW ->
                        totalWithdrawals = totalWithdrawals.add(txn.getAmount());

                case TRANSFER_IN ->
                        totalTransfersIn = totalTransfersIn.add(txn.getAmount());

                case TRANSFER_OUT ->
                        totalTransfersOut = totalTransfersOut.add(txn.getAmount());
            }
        }

        return new WalletSummaryResponse(
                userId,
                wallet.getBalance(),
                totalDeposits,
                totalWithdrawals,
                totalTransfersIn,
                totalTransfersOut
        );
    }

    // ==============================
    // 🔒 PRIVATE HELPER
    // ==============================
    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }
    }
}

