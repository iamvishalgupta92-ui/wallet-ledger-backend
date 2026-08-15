package com.vishal.walletledger.controller.wallet;

import com.vishal.walletledger.wallet.Wallet;
import com.vishal.walletledger.wallet.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Wallet> createWallet(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.createWallet(userId));
    }

    @GetMapping("/me")
    public ResponseEntity<Wallet> getMyWallet(Authentication authentication) {
        return ResponseEntity.ok(
                walletService.getWalletByEmail(authentication.getName())
        );
    }
}