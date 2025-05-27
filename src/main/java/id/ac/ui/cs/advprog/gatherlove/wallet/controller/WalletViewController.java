package id.ac.ui.cs.advprog.gatherlove.wallet.controller;

import id.ac.ui.cs.advprog.gatherlove.authentication.security.services.UserDetailsImpl;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.Transaction;
import id.ac.ui.cs.advprog.gatherlove.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletViewController {

    private final WalletService walletService;

    @GetMapping("/balance")
    public String balancePage(@AuthenticationPrincipal UserDetailsImpl user, Model model) {
        UUID userId = user.getId();
        model.addAttribute("balance", walletService.getWalletBalance(userId));
        model.addAttribute("userId", userId);
        return "wallet/balance";
    }

    @GetMapping("/topup")
    public String topUpForm(@AuthenticationPrincipal UserDetailsImpl user, Model model) {
        model.addAttribute("userId", user.getId());
        return "wallet/topup";
    }

    @GetMapping("/transactions")
    public String transactionPage(@AuthenticationPrincipal UserDetailsImpl user, Model model) {
        UUID userId = user.getId();
        model.addAttribute("txList",
                walletService.getWalletWithTransactions(userId).getTransactions());
        model.addAttribute("userId", userId);
        return "wallet/transactions";
    }

    @GetMapping("/transactions/{id}")
    public String transactionDetail(@AuthenticationPrincipal UserDetailsImpl user,
                                    @PathVariable("id") Long txId, Model model,
                                    RedirectAttributes redirect) {
        try {
            Transaction tx = walletService.getTransactionById(user.getId(), txId);
            model.addAttribute("tx", tx);
            return "wallet/transaction-detail";
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/wallet/transactions";
        }
    }
}