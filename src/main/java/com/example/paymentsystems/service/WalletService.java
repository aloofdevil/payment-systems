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
import java.util.Optional;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WalletService(
            WalletRepository walletRepository,
            TransactionRepository transactionRepository
    ) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public Wallet createWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> walletRepository.save(new Wallet(userId)));
    }

    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    @Transactional
    public Wallet deposit(Long userId, BigDecimal amount) {

        validateAmount(amount);

        Wallet wallet = getWalletByUserId(userId);

        wallet.setBalance(wallet.getBalance().add(amount));

        transactionRepository.save(
                new Transaction(userId, amount, TransactionType.DEPOSIT)
        );

        return wallet;
    }

    @Transactional
    public Wallet withdraw(Long userId, BigDecimal amount) {

        validateAmount(amount);

        Wallet wallet = getWalletByUserId(userId);

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));

        transactionRepository.save(
                new Transaction(userId, amount, TransactionType.WITHDRAW)
        );

        return wallet;
    }

    @Transactional
    public void transfer(Long fromUser, Long toUser, BigDecimal amount, String key) {

        validateAmount(amount);

        if (fromUser.equals(toUser)) {
            throw new RuntimeException("Cannot transfer to the same account");
        }

        Optional<Transaction> existing =
                transactionRepository.findByIdempotencyKey(key);

        if (existing.isPresent()) {
            return;
        }

        Wallet sender = walletRepository.findByUserId(fromUser)
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

        Wallet receiver = walletRepository.findByUserId(toUser)
                .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        transactionRepository.save(
                new Transaction(fromUser, amount, TransactionType.TRANSFER_OUT, key)
        );

        transactionRepository.save(
                new Transaction(toUser, amount, TransactionType.TRANSFER_IN)
        );
    }

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

    public WalletSummaryResponse getWalletSummary(Long userId) {

        Wallet wallet = getWalletByUserId(userId);

        List<Transaction> transactions =
                transactionRepository.findByUserId(userId, Pageable.unpaged())
                        .getContent();

        BigDecimal deposits = BigDecimal.ZERO;
        BigDecimal withdrawals = BigDecimal.ZERO;
        BigDecimal transfersIn = BigDecimal.ZERO;
        BigDecimal transfersOut = BigDecimal.ZERO;

        for (Transaction txn : transactions) {

            switch (txn.getType()) {

                case DEPOSIT ->
                        deposits = deposits.add(txn.getAmount());

                case WITHDRAW ->
                        withdrawals = withdrawals.add(txn.getAmount());

                case TRANSFER_IN ->
                        transfersIn = transfersIn.add(txn.getAmount());

                case TRANSFER_OUT ->
                        transfersOut = transfersOut.add(txn.getAmount());
            }
        }

        return new WalletSummaryResponse(
                userId,
                wallet.getBalance(),
                deposits,
                withdrawals,
                transfersIn,
                transfersOut
        );
    }

    private void validateAmount(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }
    }
}