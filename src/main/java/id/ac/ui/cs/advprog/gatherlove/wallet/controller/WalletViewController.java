package id.ac.ui.cs.advprog.gatherlove.wallet.controller;

import id.ac.ui.cs.advprog.gatherlove.wallet.service.WalletService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.UUID;

@Controller
@RequestMapping("/wallet")
public class WalletViewController {
    private final WalletService walletService;

    public WalletViewController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/balance")
    public String balancePage(@RequestParam UUID userId, Model model) {
        model.addAttribute("balance",
                walletService.getWalletBalance(userId));
        model.addAttribute("userId", userId);
        return "wallet/balance";
    }

    @GetMapping("/topup")
    public String topUpForm(@RequestParam UUID userId, Model model) {
        model.addAttribute("userId", userId);
        return "wallet/topup";
    }

    @PostMapping("/topup")
    public String topUpSubmit(@RequestParam UUID userId, @RequestParam BigDecimal amount,
                              @RequestParam String phone, @RequestParam String method,
                              RedirectAttributes ra) {

        walletService.topUp(userId, amount, phone, method);
        ra.addFlashAttribute("success",
                "Top-up berhasil menambah saldo " + amount);
        return "redirect:/wallet/balance?userId=" + userId;
    }

    @GetMapping("/transactions")
    public String transactionPage(@RequestParam UUID userId, Model model) {
        model.addAttribute("txList",
                walletService.getWalletWithTransactions(userId)
                        .getTransactions());
        model.addAttribute("userId", userId);
        return "wallet/transactions";
    }

    @GetMapping("/transactions/{id}/delete")
    public String deleteTopUp(@RequestParam UUID userId, @PathVariable Long id,
                              RedirectAttributes r) {
        walletService.deleteTopUpTransaction(userId, id);
        r.addFlashAttribute("success", "Riwayat top-up dihapus");
        return "redirect:/wallet/transactions?userId=" + userId;
    }
}
