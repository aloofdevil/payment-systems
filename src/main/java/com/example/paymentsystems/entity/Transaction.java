package com.example.paymentsystems.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "transactions",
        indexes = {
                @Index(name = "idx_user_id", columnList = "userId")
        }
)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // NEW FIELD
    @Column(unique = true)
    private String idempotencyKey;

    protected Transaction() {}

    public Transaction(Long userId, BigDecimal amount, TransactionType type) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
    }

    public Transaction(Long userId, BigDecimal amount, TransactionType type, String key) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.idempotencyKey = key;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public Long getUserId() { return userId; }

    public BigDecimal getAmount() { return amount; }

    public TransactionType getType() { return type; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public String getIdempotencyKey() { return idempotencyKey; }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}