package com.example.paymentsystems.repository;

import com.example.paymentsystems.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
