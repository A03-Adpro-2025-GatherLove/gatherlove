package id.ac.ui.cs.advprog.gatherlove.wallet.controller;

import id.ac.ui.cs.advprog.gatherlove.authentication.security.services.UserDetailsImpl;
import id.ac.ui.cs.advprog.gatherlove.wallet.dto.*;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.Transaction;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.Wallet;
import id.ac.ui.cs.advprog.gatherlove.wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public BalanceResponse getBalance() {
        UUID userId = getCurrentUserId();
        return new BalanceResponse(walletService.getWalletBalance(userId));
    }

    @PostMapping("/topup")
    public ResponseEntity<TopUpResponse> topUp(@Valid @RequestBody TopUpRequest body) {
        UUID userId = getCurrentUserId();

        Wallet wallet = walletService.topUp(userId, body.amount(), body.phone_number(), body.method());

        TopUpResponse res = new TopUpResponse(
                "Proses Top-Up Saldo Berhasil!",
                wallet.getBalance()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/transactions")
    public TransactionListResponse getTransactions() {
        UUID userId = getCurrentUserId();
        Wallet wallet = walletService.getWalletWithTransactions(userId);

        var list = wallet.getTransactions().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new TransactionListResponse(list);
    }

    private TransactionDto toDto(Transaction t) {
        return new TransactionDto(t.getId(), t.getType(), t.getAmount(), t.getTransactionDateTime());
    }

    private UUID getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var principal = (UserDetailsImpl) auth.getPrincipal();
        return principal.getId();
    }

    @DeleteMapping("/transactions/{transactionId}")
    public DeleteResponse deleteTransaction(@PathVariable Long transactionId) {
        UUID userId = getCurrentUserId();

        walletService.deleteTopUpTransaction(userId, transactionId);
        return new DeleteResponse("Proses Penghapusan Riwayat Top-Up Berhasil!");
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawResponse> withdraw(@Valid @RequestBody WithdrawRequest body) {
        UUID userId = getCurrentUserId();

        walletService.withdrawFunds(userId, body.amount());

        WithdrawResponse res = new WithdrawResponse(
                "Penarikan Dana Berhasil Diproses!"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PostMapping("/donate")
    public ResponseEntity<DonateResponse> donate(@Valid @RequestBody DonateRequest body) {
        UUID userId = getCurrentUserId();

        Wallet wallet = walletService.debit(userId, body.amount());

        DonateResponse res = new DonateResponse(
                "Nominal untuk Donasi Berhasil Dikurangi dari Saldo!",
                wallet.getBalance()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PostMapping("/topup-async")
    public CompletableFuture<TopUpResponse> topUpAsync(@RequestBody TopUpRequest body) {
        UUID userId = getCurrentUserId();

        return walletService.topUpAsync(userId, body.amount(), body.phone_number(), body.method())
                .thenApply(w -> new TopUpResponse("OK", w.getBalance()));
    }

    @GetMapping("/balance-async")
    public CompletableFuture<BalanceResponse> getBalanceAsync() {
        UUID userId = getCurrentUserId();
        return walletService.getBalanceAsync(userId)
                .thenApply(BalanceResponse::new);
    }
}