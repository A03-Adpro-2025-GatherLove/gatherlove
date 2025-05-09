package id.ac.ui.cs.advprog.gatherlove.wallet.controller;

import id.ac.ui.cs.advprog.gatherlove.wallet.model.*;
import id.ac.ui.cs.advprog.gatherlove.wallet.service.WalletService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/balance")
    public Map<String, BigDecimal> getBalance(@RequestParam Long userId) {
        BigDecimal balance = walletService.getWalletBalance(userId);
        return Collections.singletonMap("balance", balance);
    }

    @PostMapping("/topup")
    public ResponseEntity<Map<String, Object>> topUp(
            @RequestParam Long userId, @RequestBody Map<String, Object> body) {
        String method = Objects.toString(body.get("method"), "");
        String phoneNumber = Objects.toString(body.get("phone_number"), "");
        BigDecimal amount = new BigDecimal(body.get("amount").toString());

        Wallet wallet = walletService.topUp(userId, amount, phoneNumber, method);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Proses Top‑Up Saldo Berhasil!");
        response.put("new_balance", wallet.getBalance());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/transactions")
    public List<Map<String, Object>> getTransactions(@RequestParam Long userId) {
        Wallet wallet = walletService.getWalletWithTransactions(userId);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Transaction tr : wallet.getTransactions()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("transaction_id", tr.getId());
            m.put("type", tr.getType());
            m.put("amount", tr.getAmount());
            m.put("timestamp", tr.getTransactionDateTime());
            result.add(m);
        }

        return result;
    }

    @DeleteMapping("/transactions/{transactionId}")
    public Map<String, String> deleteTransaction(
            @RequestParam Long userId, @PathVariable Long transactionId) {

        walletService.deleteTopUpTransaction(userId, transactionId);
        return Collections.singletonMap("message", "Proses Penghapusan Riwayat Top‑Up Berhasil!");
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Map<String, Object>> withdraw(
            @RequestParam Long userId, @RequestBody Map<String, Object> body) {

        BigDecimal amount = new BigDecimal(body.get("amount").toString());

        walletService.withdrawFunds(userId, amount);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Penarikan Dana sedang Diproses...");
        response.put("status", "NEED_ADMINISTRATOR_APPROVAL");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}