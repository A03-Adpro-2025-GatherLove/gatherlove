package id.ac.ui.cs.advprog.gatherlove.wallet.controller;

import id.ac.ui.cs.advprog.gatherlove.authentication.security.services.UserDetailsImpl;
import id.ac.ui.cs.advprog.gatherlove.wallet.enums.TransactionType;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.*;
import id.ac.ui.cs.advprog.gatherlove.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    private UUID userId;

    private UserDetailsImpl principal;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        principal = new UserDetailsImpl(userId, "test", "test@test.com",
                "test", List.of(new SimpleGrantedAuthority("ROLE_USER")), null);
    }

    @Test
    void testGetBalance() throws Exception {
        given(walletService.getWalletBalance(userId)).willReturn(BigDecimal.valueOf(40000));

        mockMvc.perform(get("/api/wallet/balance").with(csrf()).with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(40000));
    }

    @Test
    void testTopUp() throws Exception {
        Wallet after = new Wallet(userId, BigDecimal.valueOf(60000));
        UUID requestId = UUID.randomUUID();
        given(walletService.topUp(eq(userId), eq(BigDecimal.valueOf(10000)),
                eq("081234"), eq("GOPAY"), eq(requestId))).willReturn(after);

        String body = String.format("""
        {
            "method": "GOPAY", "phone_number": "081234", "amount": 10000, "requestId": "%s"
        }
        """, requestId);

        mockMvc.perform(post("/api/wallet/topup")
                        .with(csrf()).with(user(principal))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.new_balance").value(60000))
                .andExpect(jsonPath("$.message")
                        .value("Proses Top-Up Saldo Berhasil!"));
    }

    @Test
    void getTransactions() throws Exception {
        Wallet w = new Wallet(userId, BigDecimal.valueOf(50000));
        Transaction tx = new Transaction(TransactionType.TOP_UP, BigDecimal.valueOf(10000), "GOPAY");
        tx.setId(1L);
        w.addTransaction(tx);

        given(walletService.getWalletWithTransactions(userId)).willReturn(w);

        mockMvc.perform(get("/api/wallet/transactions")
                        .with(csrf()).with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transactions.length()").value(1))
                .andExpect(jsonPath("$.transactions[0].transaction_id").value(1))
                .andExpect(jsonPath("$.transactions[0].type").value("TOP_UP"))
                .andExpect(jsonPath("$.transactions[0].amount").value(10000));
    }

    @Test
    void testDeleteTopUpTransaction() throws Exception {
        willDoNothing().given(walletService).deleteTopUpTransaction(userId, 999L);

        mockMvc.perform(delete("/api/wallet/transactions/999")
                        .with(csrf()).with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Proses Penghapusan Riwayat Top-Up Berhasil!"));

        then(walletService).should().deleteTopUpTransaction(userId, 999L);
    }

    @Test
    void testWithdraw() throws Exception {
        Wallet after = new Wallet(userId, BigDecimal.valueOf(4000));
        given(walletService.withdrawFunds(userId, BigDecimal.valueOf(1000))).willReturn(after);

        String body = """
            { "amount": 1000 }
            """;

        mockMvc.perform(post("/api/wallet/withdraw")
                        .with(csrf()).with(user(principal))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value("Penarikan Dana Berhasil Diproses!"));
    }

    @Test
    void testDonate() throws Exception {
        Wallet afterDonate = new Wallet(userId, BigDecimal.valueOf(10000));
        given(walletService.debit(userId, BigDecimal.valueOf(20000))).willReturn(afterDonate);

        String body = """
            { "amount": 20000 }
            """;

        mockMvc.perform(post("/api/wallet/donate")
                        .with(csrf()).with(user(principal))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value("Nominal untuk Donasi Berhasil Dikurangi dari Saldo!"))
                .andExpect(jsonPath("$.remaining_balance").value(10000));
    }
}
