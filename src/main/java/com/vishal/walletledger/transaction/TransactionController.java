package com.vishal.walletledger.transaction;

import com.vishal.walletledger.repository.TransactionRepository;
import com.vishal.walletledger.repository.wallet.WalletRepository;
import com.vishal.walletledger.service.IdempotencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final IdempotencyService idempotencyService;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    public TransactionController(
            TransactionService transactionService,
            IdempotencyService idempotencyService,
            TransactionRepository transactionRepository,
            WalletRepository walletRepository) {

        this.transactionService = transactionService;
        this.idempotencyService = idempotencyService;
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }

    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<List<Transaction>> getWalletTransactions(
            @PathVariable Long walletId,
            Authentication authentication) {

        String userEmail = authentication.getName();

        var wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (!wallet.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException(
                    "You are not allowed to access this wallet"
            );
        }

        return ResponseEntity.ok(
                transactionRepository.findByWalletId(walletId)
        );
    }

    @PostMapping("/deposit/{walletId}")
    public ResponseEntity<Transaction> deposit(
            @PathVariable Long walletId,
            @RequestParam BigDecimal amount,
            Authentication authentication) {

        String userEmail = authentication.getName();

        return ResponseEntity.ok(
                transactionService.deposit(walletId, amount, userEmail)
        );
    }

    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transfer(
            @RequestParam Long fromWalletId,
            @RequestParam Long toWalletId,
            @RequestParam BigDecimal amount,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            Authentication authentication) {

        if (idempotencyService.exists(idempotencyKey)) {
            return ResponseEntity.badRequest().build();
        }

        String userEmail = authentication.getName();

        Transaction transaction = transactionService.transfer(
                fromWalletId,
                toWalletId,
                amount,
                userEmail
        );

        idempotencyService.save(idempotencyKey);

        return ResponseEntity.ok(transaction);
    }
}