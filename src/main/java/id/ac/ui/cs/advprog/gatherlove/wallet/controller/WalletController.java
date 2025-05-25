package id.ac.ui.cs.advprog.gatherlove.wallet.controller;

import id.ac.ui.cs.advprog.gatherlove.authentication.security.services.UserDetailsImpl;
import id.ac.ui.cs.advprog.gatherlove.wallet.dto.*;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.Transaction;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.Wallet;
import id.ac.ui.cs.advprog.gatherlove.wallet.service.WalletService;
import id.ac.ui.cs.advprog.gatherlove.wallet.repository.TransactionRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    private final TransactionRepository transactionRepository;

    public WalletController(WalletService walletService, TransactionRepository transactionRepository) {
        this.walletService = walletService;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/balance")
    public BalanceResponse getBalance() {
        UUID userId = getCurrentUserId();
        return new BalanceResponse(walletService.getWalletBalance(userId));
    }

    @PostMapping("/topup")
    public ResponseEntity<TopUpResponse> topUp(@Valid @RequestBody TopUpRequest body) {
        UUID userId = getCurrentUserId();

        Wallet wallet = walletService.topUp(userId, body.amount(), body.phone_number(),
                                            body.method(), body.requestId());

        TopUpResponse res = new TopUpResponse(
                "Proses Top-Up Saldo Berhasil!", wallet.getBalance()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/transactions")
    public TransactionListResponse getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        UUID userId = getCurrentUserId();

        PageRequest pr = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "transactionDateTime"));

        Page<Transaction> slice = transactionRepository.findByWalletUserId(userId, pr);

        var dto = slice.getContent()
                .stream()
                .map(this::toDto)
                .toList();

        return new TransactionListResponse(dto);
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

        walletService.withdrawFunds(userId, body.amount(), body.requestId());

        WithdrawResponse res = new WithdrawResponse(
                "Penarikan Dana Berhasil Diproses!"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PostMapping("/donate")
    public ResponseEntity<DonateResponse> donate(@Valid @RequestBody DonateRequest body) {
        UUID userId = getCurrentUserId();

        Wallet wallet = walletService.debit(userId, body.amount(), body.requestId());

        DonateResponse res = new DonateResponse(
                "Nominal untuk Donasi Berhasil Dikurangi dari Saldo!",
                wallet.getBalance()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
}