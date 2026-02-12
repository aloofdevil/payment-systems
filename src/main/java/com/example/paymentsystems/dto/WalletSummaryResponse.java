package com.example.paymentsystems.dto;

import java.math.BigDecimal;

public class WalletSummaryResponse {

    private Long userId;
    private BigDecimal currentBalance;
    private BigDecimal totalDeposits;
    private BigDecimal totalWithdrawals;
    private BigDecimal totalTransfersIn;
    private BigDecimal totalTransfersOut;

    public WalletSummaryResponse(
            Long userId,
            BigDecimal currentBalance,
            BigDecimal totalDeposits,
            BigDecimal totalWithdrawals,
            BigDecimal totalTransfersIn,
            BigDecimal totalTransfersOut) {

        this.userId = userId;
        this.currentBalance = currentBalance;
        this.totalDeposits = totalDeposits;
        this.totalWithdrawals = totalWithdrawals;
        this.totalTransfersIn = totalTransfersIn;
        this.totalTransfersOut = totalTransfersOut;
    }

    public Long getUserId() { return userId; }
    public BigDecimal getCurrentBalance() { return currentBalance; }
    public BigDecimal getTotalDeposits() { return totalDeposits; }
    public BigDecimal getTotalWithdrawals() { return totalWithdrawals; }
    public BigDecimal getTotalTransfersIn() { return totalTransfersIn; }
    public BigDecimal getTotalTransfersOut() { return totalTransfersOut; }
}
