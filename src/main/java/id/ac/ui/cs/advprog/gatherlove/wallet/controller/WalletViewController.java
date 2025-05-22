package id.ac.ui.cs.advprog.gatherlove.wallet.controller;

import id.ac.ui.cs.advprog.gatherlove.authentication.security.services.UserDetailsImpl;
import id.ac.ui.cs.advprog.gatherlove.wallet.service.WalletService;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public String balancePage(Model model) {
        UUID userId = getCurrentUserId();
        model.addAttribute("balance",
                walletService.getWalletBalance(userId));
        model.addAttribute("userId", userId);
        return "wallet/balance";
    }

    @GetMapping("/topup")
    public String topUpForm(Model model) {
        UUID userId = getCurrentUserId();
        model.addAttribute("userId", userId);
        return "wallet/topup";
    }

    @PostMapping("/topup")
    public String topUpSubmit(@RequestParam BigDecimal amount, @RequestParam String phone,
                              @RequestParam String method, RedirectAttributes ra) {
        UUID userId = getCurrentUserId();
        walletService.topUp(userId, amount, phone, method);
        ra.addFlashAttribute("success",
                "Top-up berhasil menambah saldo " + amount);
        return "redirect:/wallet/balance?userId=" + userId;
    }

    @GetMapping("/transactions")
    public String transactionPage(Model model) {
        UUID userId = getCurrentUserId();
        model.addAttribute("txList",
                walletService.getWalletWithTransactions(userId).getTransactions());
        model.addAttribute("userId", userId);
        return "wallet/transactions";
    }

    @GetMapping("/transactions/{id}/delete")
    public String deleteTopUp(@PathVariable Long id, RedirectAttributes r) {
        UUID userId = getCurrentUserId();
        walletService.deleteTopUpTransaction(userId, id);
        r.addFlashAttribute("success", "Riwayat top-up dihapus");
        return "redirect:/wallet/transactions?userId=" + userId;
    }

    private UUID getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var principal = (UserDetailsImpl) auth.getPrincipal();
        return principal.getId();
    }
}
