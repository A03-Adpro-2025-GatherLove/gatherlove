package id.ac.ui.cs.advprog.gatherlove.wallet.controller;

import id.ac.ui.cs.advprog.gatherlove.wallet.enums.TransactionType;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.*;
import id.ac.ui.cs.advprog.gatherlove.wallet.service.WalletService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import java.math.BigDecimal;
import java.util.UUID;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    private UUID userID = UUID.randomUUID();

    @Test
    void testGetBalance() throws Exception {
        given(walletService.getWalletBalance(userID)).willReturn(BigDecimal.valueOf(40000));

        mockMvc.perform(get("/api/wallet/balance").param("userId", String.valueOf(userID))
                        .with(csrf()).with(user("testuser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(40000));
    }

    @Test
    void testTopUp() throws Exception {
        UUID userID = UUID.randomUUID();
        Wallet walletPostTopUp = new Wallet(userID, BigDecimal.valueOf(60000));
        given(walletService.topUp(eq(userID), eq(BigDecimal.valueOf(10000)),
                eq("081234"), eq("GOPAY"))).willReturn(walletPostTopUp);

        String json = """
            { "method":"GOPAY", "phone_number":"081234", "amount":10000 }
            """;

        mockMvc.perform(post("/api/wallet/topup").param("userId", String.valueOf(userID))
                        .with(csrf()).with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.new_balance").value(60000))
                .andExpect(jsonPath("$.message").value("Proses Top-Up Saldo Berhasil!"));
    }

    @Test
    void testGetTransactions() throws Exception {
        UUID userID = UUID.randomUUID();
        Wallet wallet = new Wallet(userID, BigDecimal.valueOf(50000));

        Transaction tx = new Transaction();
        tx.setId(1L);
        tx.setType(TransactionType.TOP_UP);
        tx.setAmount(BigDecimal.valueOf(10000));
        tx.setPaymentMethod("GOPAY");
        wallet.addTransaction(tx);

        given(walletService.getWalletWithTransactions(userID)).willReturn(wallet);

        mockMvc.perform(get("/api/wallet/transactions").param("userId", String.valueOf(userID))
                        .with(csrf()).with(csrf()).with(user("testuser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transactions.length()").value(1))
                .andExpect(jsonPath("$.transactions[0].transaction_id").value(1))
                .andExpect(jsonPath("$.transactions[0].type").value("TOP_UP"))
                .andExpect(jsonPath("$.transactions[0].amount").value(10000));
    }

    @Test
    void testDeleteTopUpTransaction() throws Exception {
        UUID userID = UUID.randomUUID();
        willDoNothing().given(walletService).deleteTopUpTransaction(userID, 999L);

        mockMvc.perform(delete("/api/wallet/transactions/999").param("userId", String.valueOf(userID))
                        .with(csrf()).with(csrf()).with(user("testuser").roles("USER")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.message")
                        .value("Proses Penghapusan Riwayat Top-Up Berhasil!"));

        then(walletService).should(times(1))
                .deleteTopUpTransaction(userID, 999L);
    }

    @Test
    void testWithdraw() throws Exception {
        UUID userID = UUID.randomUUID();
        Wallet walletAfterWithdraw = new Wallet(userID, BigDecimal.valueOf(4000));
        given(walletService.withdrawFunds(userID, BigDecimal.valueOf(1000))).willReturn(walletAfterWithdraw);

        String json = """
            { "campaign_id": 7, "amount": 1000 }
            """;

        mockMvc.perform(post("/api/wallet/withdraw").param("userId", String.valueOf(userID))
                        .with(csrf()).with(csrf()).with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Penarikan Dana Berhasil Diproses!"));
    }

    @Test
    void testDonate() throws Exception {
        Wallet afterDonate = new Wallet(userID, BigDecimal.valueOf(10000));
        given(walletService.debit(userID, BigDecimal.valueOf(20000)))
                .willReturn(afterDonate);

        String json = """
        { "amount": 20000 }
        """;

        mockMvc.perform(post("/api/wallet/donate").param("userId", String.valueOf(userID))
                        .with(csrf()).with(csrf()).with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value("Nominal untuk Donasi Berhasil Dikurangi dari Saldo!"))
                .andExpect(jsonPath("$.remaining_balance").value(10000));
    }
}