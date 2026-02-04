package com.example.paymentsystems.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;   // DEPOSIT, WITHDRAW

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    // JPA requires this
    protected Transaction() {}

    public Transaction(Long userId, BigDecimal amount, TransactionType type) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
