package com.vishal.walletledger.kafka;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionEvent(
        Long transactionId,
        Long walletId,
        BigDecimal amount,
        String type,
        String status,
        LocalDateTime createdAt
) {
}