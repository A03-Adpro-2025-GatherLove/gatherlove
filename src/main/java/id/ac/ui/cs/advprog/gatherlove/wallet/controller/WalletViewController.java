package id.ac.ui.cs.advprog.gatherlove.wallet.controller;

import id.ac.ui.cs.advprog.gatherlove.authentication.security.services.UserDetailsImpl;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.Transaction;
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

    @GetMapping("/transactions")
    public String transactionPage(Model model) {
        UUID userId = getCurrentUserId();
        model.addAttribute("txList",
                walletService.getWalletWithTransactions(userId).getTransactions());
        model.addAttribute("userId", userId);
        return "wallet/transactions";
    }

    @GetMapping("/transactions/{id}")
    public String transactionDetail(
            @PathVariable("id") Long txId,
            Model model,
            RedirectAttributes redirect
    ) {
        UUID userId = getCurrentUserId();
        try {
            Transaction tx = walletService.getTransactionById(userId, txId);
            model.addAttribute("tx", tx);
            return "wallet/transaction-detail";
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/wallet/transactions";
        }
    }


    private UUID getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var principal = (UserDetailsImpl) auth.getPrincipal();
        return principal.getId();
    }
}
