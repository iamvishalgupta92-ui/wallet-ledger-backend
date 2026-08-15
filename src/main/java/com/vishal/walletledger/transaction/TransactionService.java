package com.vishal.walletledger.transaction;

import com.vishal.walletledger.kafka.TransactionEvent;
import com.vishal.walletledger.kafka.TransactionProducer;
import com.vishal.walletledger.repository.TransactionRepository;
import com.vishal.walletledger.repository.wallet.WalletRepository;
import com.vishal.walletledger.wallet.Wallet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final TransactionProducer transactionProducer;

    public TransactionService(
            TransactionRepository transactionRepository,
            WalletRepository walletRepository,
            TransactionProducer transactionProducer) {

        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.transactionProducer = transactionProducer;
    }

    @Transactional
    public Transaction deposit(
            Long walletId,
            BigDecimal amount,
            String userEmail) {

        Wallet wallet = walletRepository.findByIdForUpdate(walletId)
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found"));

        if (!wallet.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException(
                    "You are not allowed to access this wallet");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(
                    "Amount must be greater than zero");
        }

        wallet.setBalance(
                wallet.getBalance().add(amount)
        );

        walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setType("DEPOSIT");
        transaction.setStatus("SUCCESS");
        transaction.setCreatedAt(LocalDateTime.now());

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        TransactionEvent event = new TransactionEvent(
                savedTransaction.getId(),
                walletId,
                amount,
                "DEPOSIT",
                "SUCCESS",
                savedTransaction.getCreatedAt()
        );

        transactionProducer.sendTransactionEvent(event);

        return savedTransaction;
    }

    @Transactional
    public Transaction transfer(
            Long fromWalletId,
            Long toWalletId,
            BigDecimal amount,
            String userEmail) {

        Wallet fromWallet = walletRepository.findByIdForUpdate(fromWalletId)
                .orElseThrow(() ->
                        new RuntimeException("Sender wallet not found"));

        Wallet toWallet = walletRepository.findByIdForUpdate(toWalletId)
                .orElseThrow(() ->
                        new RuntimeException("Receiver wallet not found"));

        if (!fromWallet.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException(
                    "You are not allowed to access this wallet");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(
                    "Amount must be greater than zero");
        }

        if (fromWallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException(
                    "Insufficient balance");
        }

        fromWallet.setBalance(
                fromWallet.getBalance().subtract(amount)
        );

        toWallet.setBalance(
                toWallet.getBalance().add(amount)
        );

        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        Transaction transaction = new Transaction();
        transaction.setWallet(fromWallet);
        transaction.setAmount(amount);
        transaction.setType("TRANSFER");
        transaction.setStatus("SUCCESS");
        transaction.setCreatedAt(LocalDateTime.now());

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        TransactionEvent event = new TransactionEvent(
                savedTransaction.getId(),
                fromWalletId,
                amount,
                "TRANSFER",
                "SUCCESS",
                savedTransaction.getCreatedAt()
        );

        transactionProducer.sendTransactionEvent(event);

        return savedTransaction;
    }
}