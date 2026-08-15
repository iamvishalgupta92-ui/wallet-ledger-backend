package com.vishal.walletledger.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TransactionProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper) {

        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendTransactionEvent(TransactionEvent event) {

        try {
            String json = objectMapper.writeValueAsString(event);

            kafkaTemplate.send("wallet-transactions", json);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert transaction event to JSON", e);
        }
    }
}