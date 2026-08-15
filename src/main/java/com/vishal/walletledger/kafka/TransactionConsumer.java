package com.vishal.walletledger.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionConsumer {

    @KafkaListener(
            topics = "wallet-transactions",
            groupId = "wallet-ledger-group"
    )
    public void consumeTransaction(String message) {
        System.out.println("Kafka Event Received: " + message);
    }
}