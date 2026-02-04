package com.example.paymentsystems.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Double amount;

    private String type; // DEPOSIT, WITHDRAW

    private LocalDateTime createdAt;

    public Transaction() {}

    public Transaction(Long userId, Double amount, String type) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
