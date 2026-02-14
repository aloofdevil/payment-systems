package com.example.paymentsystems.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TransferRequest {

    @NotNull(message = "From user is required")
    private Long fromUser;

    @NotNull(message = "To user is required")
    private Long toUser;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    // ✅ GETTERS & SETTERS

    public Long getFromUser() {
        return fromUser;
    }

    public void setFromUser(Long fromUser) {
        this.fromUser = fromUser;
    }

    public Long getToUser() {
        return toUser;
    }

    public void setToUser(Long toUser) {
        this.toUser = toUser;
    }

    public BigDecimal getAmount() {   // ✅ FIXED
        return amount;
    }

    public void setAmount(BigDecimal amount) {   // ✅ FIXED
        this.amount = amount;
    }
}
