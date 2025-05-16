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

import java.math.BigDecimal;
import java.util.Collections;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @Test
    void testGetBalance() throws Exception {
        given(walletService.getWalletBalance(123L)).willReturn(BigDecimal.valueOf(40000));

        mockMvc.perform(get("/api/wallet/balance").param("userId", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(40000));
    }

    @Test
    void testTopUp() throws Exception {
        Wallet walletPostTopUp = new Wallet(123L, BigDecimal.valueOf(60000));
        given(walletService.topUp(eq(123L), eq(BigDecimal.valueOf(10000)),
                eq("081234"), eq("GOPAY"))).willReturn(walletPostTopUp);

        String json = """
            { "method":"GOPAY", "phone_number":"081234", "amount":10000 }
            """;

        mockMvc.perform(post("/api/wallet/topup").param("userId", "123")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.new_balance").value(60000))
                .andExpect(jsonPath("$.message").value("Proses Top-Up Saldo Berhasil!"));
    }

    @Test
    void testGetTransactions() throws Exception {
        Wallet wallet = new Wallet(123L, BigDecimal.valueOf(50000));

        Transaction tx = new Transaction();
        tx.setId(1L);
        tx.setType(TransactionType.TOP_UP);
        tx.setAmount(BigDecimal.valueOf(10000));
        tx.setPaymentMethod("GOPAY");
        wallet.addTransaction(tx);

        given(walletService.getWalletWithTransactions(123L)).willReturn(wallet);

        mockMvc.perform(get("/api/wallet/transactions").param("userId", "123"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transactions.length()").value(1))
                .andExpect(jsonPath("$.transactions[0].transaction_id").value(1))
                .andExpect(jsonPath("$.transactions[0].type").value("TOP_UP"))
                .andExpect(jsonPath("$.transactions[0].amount").value(10000));
    }

    @Test
    void testDeleteTopUpTransaction() throws Exception {
        willDoNothing().given(walletService).deleteTopUpTransaction(123L, 999L);

        mockMvc.perform(delete("/api/wallet/transactions/999").param("userId", "123"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.message")
                        .value("Proses Penghapusan Riwayat Top-Up Berhasil!"));

        then(walletService).should(times(1))
                .deleteTopUpTransaction(123L, 999L);
    }

    @Test
    void testWithdraw() throws Exception {
        Wallet walletAfterWithdraw = new Wallet(123L, BigDecimal.valueOf(4000));
        given(walletService.withdrawFunds(123L, BigDecimal.valueOf(1000))).willReturn(walletAfterWithdraw);

        String json = """
            { "campaign_id": 7, "amount": 1000 }
            """;

        mockMvc.perform(post("/api/wallet/withdraw").param("userId", "123")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Penarikan Dana sedang Diproses..."))
                .andExpect(jsonPath("$.status").value("NEED_ADMINISTRATOR_APPROVAL"));
    }
}