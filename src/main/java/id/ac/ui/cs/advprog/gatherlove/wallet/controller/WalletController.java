package id.ac.ui.cs.advprog.gatherlove.wallet.controller;

import id.ac.ui.cs.advprog.gatherlove.authentication.security.services.UserDetailsImpl;
import id.ac.ui.cs.advprog.gatherlove.wallet.dto.*;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.Transaction;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.Wallet;
import id.ac.ui.cs.advprog.gatherlove.wallet.service.WalletService;
import id.ac.ui.cs.advprog.gatherlove.wallet.repository.TransactionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final TransactionRepository transactionRepository;

    @GetMapping("/balance")
    public BalanceResponse getBalance(@AuthenticationPrincipal UserDetailsImpl user) {
        UUID userId = user.getId();
        return new BalanceResponse(walletService.getWalletBalance(userId));
    }

    @PostMapping("/topup")
    public ResponseEntity<TopUpResponse> topUp(@AuthenticationPrincipal UserDetailsImpl user,
                                               @Valid @RequestBody TopUpRequest body) {
        Wallet wallet = walletService.topUp(
                user.getId(),
                body.amount(),
                body.phone_number(),
                body.method(),
                body.requestId()
        );

        TopUpResponse res = new TopUpResponse(
                "Proses Top-Up Saldo Berhasil!",
                wallet.getBalance()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/transactions")
    public TransactionListResponse getTransactions(@AuthenticationPrincipal UserDetailsImpl user,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        PageRequest pr = PageRequest.of(page, size,Sort.by(Sort.Direction.DESC, "transactionDateTime"));
        Page<Transaction> slice = transactionRepository.findByWalletUserId(user.getId(), pr);

        var dto = slice.getContent().stream().map(this::toDto).toList();

        return new TransactionListResponse(dto);
    }

    @DeleteMapping("/transactions/{transactionId}")
    public DeleteResponse deleteTransaction(@AuthenticationPrincipal UserDetailsImpl user,
                                            @PathVariable Long transactionId) {
        walletService.deleteTopUpTransaction(user.getId(), transactionId);
        return new DeleteResponse("Proses Penghapusan Riwayat Top-Up Berhasil!");
    }

    private TransactionDto toDto(Transaction t) {
        return new TransactionDto(t.getId(), t.getType(), t.getAmount(), t.getTransactionDateTime());
    }
}