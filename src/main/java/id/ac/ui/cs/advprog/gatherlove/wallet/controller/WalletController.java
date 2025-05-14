package id.ac.ui.cs.advprog.gatherlove.wallet.controller;

import id.ac.ui.cs.advprog.gatherlove.wallet.dto.*;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.Transaction;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.Wallet;
import id.ac.ui.cs.advprog.gatherlove.wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/balance")
    public BalanceResponse getBalance(@RequestParam Long userId) {
        return new BalanceResponse(walletService.getWalletBalance(userId));
    }

    @PostMapping("/topup")
    public ResponseEntity<TopUpResponse> topUp(
            @RequestParam Long userId,
            @Valid @RequestBody TopUpRequest body) {

        Wallet wallet = walletService.topUp(
                userId,
                body.amount(),
                body.phone_number(),
                body.method()
        );

        TopUpResponse res = new TopUpResponse(
                "Proses Top-Up Saldo Berhasil!",
                wallet.getBalance()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/transactions")
    public TransactionListResponse getTransactions(@RequestParam Long userId) {
        Wallet wallet = walletService.getWalletWithTransactions(userId);

        var list = wallet.getTransactions().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new TransactionListResponse(list);
    }

    private TransactionDto toDto(Transaction t) {
        return new TransactionDto(
                t.getId(),
                t.getType(),
                t.getAmount(),
                t.getTransactionDateTime()
        );
    }

    @DeleteMapping("/transactions/{transactionId}")
    public DeleteResponse deleteTransaction(
            @RequestParam Long userId,
            @PathVariable Long transactionId) {

        walletService.deleteTopUpTransaction(userId, transactionId);
        return new DeleteResponse("Proses Penghapusan Riwayat Top-Up Berhasil!");
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawResponse> withdraw(
            @RequestParam Long userId,
            @Valid @RequestBody WithdrawRequest body) {

        walletService.withdrawFunds(userId, body.amount());

        WithdrawResponse res = new WithdrawResponse(
                "Penarikan Dana sedang Diproses...",
                "NEED_ADMINISTRATOR_APPROVAL"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
}
