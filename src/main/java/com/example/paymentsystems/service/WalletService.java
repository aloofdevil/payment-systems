package com.example.paymentsystems.service;

import com.example.paymentsystems.dto.WalletSummaryResponse;
import com.example.paymentsystems.entity.Transaction;
import com.example.paymentsystems.entity.TransactionType;
import com.example.paymentsystems.entity.Wallet;
import com.example.paymentsystems.repository.TransactionRepository;
import com.example.paymentsystems.repository.WalletRepository;
import jakarta.transaction.Transactional;
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

    // ✅ CREATE WALLET (if not exists)
    public Wallet createWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> walletRepository.save(new Wallet(userId)));
    }

    // ✅ GET WALLET
    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    // ✅ DEPOSIT
    @Transactional
    public Wallet deposit(Long userId, Double amount) {

        if (amount == null || amount <= 0) {
            throw new RuntimeException("Deposit amount must be greater than zero");
        }

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        BigDecimal depositAmount = BigDecimal.valueOf(amount);

        // Update balance
        wallet.setBalance(wallet.getBalance().add(depositAmount));
        walletRepository.save(wallet);

        // Save transaction
        Transaction txn = new Transaction();
        txn.setUserId(userId);
        txn.setAmount(depositAmount);
        txn.setType(TransactionType.DEPOSIT);
        transactionRepository.save(txn);

        return wallet;
    }

    // ✅ WITHDRAW
    @Transactional
    public Wallet withdraw(Long userId, Double amount) {

        if (amount == null || amount <= 0) {
            throw new RuntimeException("Withdraw amount must be greater than zero");
        }

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        BigDecimal withdrawAmount = BigDecimal.valueOf(amount);

        if (wallet.getBalance().compareTo(withdrawAmount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(withdrawAmount));
        walletRepository.save(wallet);

        // Save transaction
        Transaction txn = new Transaction();
        txn.setUserId(userId);
        txn.setAmount(withdrawAmount);
        txn.setType(TransactionType.WITHDRAW);
        transactionRepository.save(txn);

        return wallet;
    }

    // ✅ TRANSFER
    @Transactional
    public Wallet transfer(Long fromUser, Long toUser, Double amount) {

        if (amount == null || amount <= 0) {
            throw new RuntimeException("Transfer amount must be greater than zero");
        }

        Wallet sender = walletRepository.findByUserId(fromUser)
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

        Wallet receiver = walletRepository.findByUserId(toUser)
                .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

        BigDecimal transferAmount = BigDecimal.valueOf(amount);

        if (sender.getBalance().compareTo(transferAmount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Deduct from sender
        sender.setBalance(sender.getBalance().subtract(transferAmount));
        walletRepository.save(sender);

        // Add to receiver
        receiver.setBalance(receiver.getBalance().add(transferAmount));
        walletRepository.save(receiver);

        // Save sender transaction (WITHDRAW)
        Transaction debitTxn = new Transaction();
        debitTxn.setUserId(fromUser);
        debitTxn.setAmount(transferAmount);
        debitTxn.setType(TransactionType.WITHDRAW);
        transactionRepository.save(debitTxn);

        // Save receiver transaction (DEPOSIT)
        Transaction creditTxn = new Transaction();
        creditTxn.setUserId(toUser);
        creditTxn.setAmount(transferAmount);
        creditTxn.setType(TransactionType.DEPOSIT);
        transactionRepository.save(creditTxn);

        return sender;
    }

    // ✅ GET TRANSACTIONS FOR A USER
    public List<Transaction> getTransactionsByUser(Long userId) {
        return transactionRepository.findByUserId(userId);
    }
    public WalletSummaryResponse getWalletSummary(Long userId) {

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        List<Transaction> transactions = transactionRepository.findByUserId(userId);

        BigDecimal totalDeposits = BigDecimal.ZERO;
        BigDecimal totalWithdrawals = BigDecimal.ZERO;
        BigDecimal totalTransfersIn = BigDecimal.ZERO;
        BigDecimal totalTransfersOut = BigDecimal.ZERO;

        for (Transaction txn : transactions) {
            switch (txn.getType()) {
                case DEPOSIT -> totalDeposits = totalDeposits.add(txn.getAmount());
                case WITHDRAW -> totalWithdrawals = totalWithdrawals.add(txn.getAmount());
                case TRANSFER_IN -> totalTransfersIn = totalTransfersIn.add(txn.getAmount());
                case TRANSFER_OUT -> totalTransfersOut = totalTransfersOut.add(txn.getAmount());
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

}
