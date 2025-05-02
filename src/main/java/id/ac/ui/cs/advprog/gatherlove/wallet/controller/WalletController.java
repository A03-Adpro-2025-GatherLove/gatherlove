package id.ac.ui.cs.advprog.gatherlove.wallet.controller;

import id.ac.ui.cs.advprog.gatherlove.wallet.model.Wallet;
import id.ac.ui.cs.advprog.gatherlove.wallet.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/{userId}/balance")
    public BigDecimal getBalance(@PathVariable Long userId) {
        return walletService.getWalletBalance(userId);
    }

    @PostMapping("/{userId}/topup")
    public Wallet topUp(@PathVariable Long userId, @RequestParam BigDecimal amount,
            @RequestParam String phoneNumber, @RequestParam String method) {
        return walletService.topUp(userId, amount, phoneNumber, method);
    }

    @GetMapping("/{userId}/transactions")
    public Wallet getWalletWithTransactions(@PathVariable Long userId) {
        return walletService.getWalletWithTransactions(userId);
    }

    @DeleteMapping("/{userId}/transactions/{transactionId}")
    public void deleteTopUpTransaction(@PathVariable Long userId, @PathVariable Long transactionId) {
        walletService.deleteTopUpTransaction(userId, transactionId);
    }

    @PostMapping("/{userId}/withdraw")
    public Wallet withdraw(@PathVariable Long userId, @RequestParam BigDecimal amount) {
        return walletService.withdrawFunds(userId, amount);
    }
}