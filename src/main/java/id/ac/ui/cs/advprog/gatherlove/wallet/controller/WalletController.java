package id.ac.ui.cs.advprog.gatherlove.wallet.controller;

import id.ac.ui.cs.advprog.gatherlove.wallet.dto.*;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.Transaction;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.Wallet;
import id.ac.ui.cs.advprog.gatherlove.wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/balance")
    public BalanceResponse getBalance(@RequestParam UUID userId) {
        return new BalanceResponse(walletService.getWalletBalance(userId));
    }

    @PostMapping("/topup")
    public ResponseEntity<TopUpResponse> topUp(
            @RequestParam UUID userId,
            @Valid @RequestBody TopUpRequest body) {

        Wallet wallet = walletService.topUp(userId, body.amount(), body.phone_number(), body.method());

        TopUpResponse res = new TopUpResponse(
                "Proses Top-Up Saldo Berhasil!",
                wallet.getBalance()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/transactions")
    public TransactionListResponse getTransactions(@RequestParam UUID userId) {
        Wallet wallet = walletService.getWalletWithTransactions(userId);

        var list = wallet.getTransactions().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new TransactionListResponse(list);
    }

    private TransactionDto toDto(Transaction t) {
        return new TransactionDto(t.getId(), t.getType(), t.getAmount(), t.getTransactionDateTime());
    }

    @DeleteMapping("/transactions/{transactionId}")
    public DeleteResponse deleteTransaction(
            @RequestParam UUID userId,
            @PathVariable Long transactionId) {

        walletService.deleteTopUpTransaction(userId, transactionId);
        return new DeleteResponse("Proses Penghapusan Riwayat Top-Up Berhasil!");
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawResponse> withdraw(
            @RequestParam UUID userId,
            @Valid @RequestBody WithdrawRequest body) {

        walletService.withdrawFunds(userId, body.amount());

        WithdrawResponse res = new WithdrawResponse(
                "Penarikan Dana Berhasil Diproses!"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PostMapping("/donate")
    public ResponseEntity<DonateResponse> donate(
            @RequestParam UUID userId,
            @Valid @RequestBody DonateRequest body) {

        Wallet wallet = walletService.debit(userId, body.amount());

        DonateResponse res = new DonateResponse(
                "Nominal untuk Donasi Berhasil Dikurangi dari Saldo!",
                wallet.getBalance()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/balance-async")
    public CompletableFuture<BalanceResponse> getBalanceAsync(@RequestParam UUID userId) {
        return walletService.getBalanceAsync(userId)
                .thenApply(BalanceResponse::new);
    }

    @PostMapping("/topup-async")
    public CompletableFuture<TopUpResponse> topUpAsync(@RequestParam UUID userId,
                                                       @RequestBody TopUpRequest body) {

        return walletService.topUpAsync(userId, body.amount(), body.phone_number(), body.method())
                .thenApply(w -> new TopUpResponse("OK", w.getBalance()));
    }
}