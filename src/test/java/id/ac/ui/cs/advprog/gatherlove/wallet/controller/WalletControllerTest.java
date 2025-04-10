package id.ac.ui.cs.advprog.gatherlove.wallet.controller;

import id.ac.ui.cs.advprog.gatherlove.wallet.service.WalletService;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.Wallet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(WalletController.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @Test
    void testGetBalance() throws Exception {
        given(walletService.getWalletBalance(123L)).willReturn(BigDecimal.valueOf(50000));

        mockMvc.perform(get("/api/v1/wallet/123/balance"))
                .andExpect(status().isOk()).andExpect(content().string("50000"));
    }

    @Test
    void testTopUp() throws Exception {
        Wallet mockWallet = new Wallet(123L, BigDecimal.valueOf(60000));
        given(walletService.topUp(eq(123L), eq(BigDecimal.valueOf(10000)), eq("081234"), eq("GOPAY")))
                .willReturn(mockWallet);

        mockMvc.perform(post("/api/v1/wallet/123/topup")
                        .param("amount", "10000")
                        .param("phoneNumber", "081234")
                        .param("method", "GOPAY")
                )
                .andExpect(status().isOk()).andExpect(jsonPath("$.userId").value(123))
                .andExpect(jsonPath("$.balance").value(60000));
    }

    @Test
    void testGetTransactions() throws Exception {
        Wallet mockWallet = new Wallet(123L, BigDecimal.valueOf(50000));
        given(walletService.getWalletWithTransactions(123L)).willReturn(mockWallet);

        mockMvc.perform(get("/api/v1/wallet/123/transactions"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.userId").value(123))
                .andExpect(jsonPath("$.balance").value(50000));
    }

    @Test
    void testDeleteTopUpTransaction() throws Exception {
        willDoNothing().given(walletService).deleteTopUpTransaction(123L, 999L);

        mockMvc.perform(delete("/api/v1/wallet/123/transactions/888"))
                .andExpect(status().isOk());
    }

    @Test
    void testWithdraw() throws Exception {
        Wallet mockWallet = new Wallet(123L, BigDecimal.valueOf(40000));
        given(walletService.withdrawFunds(eq(123L), eq(BigDecimal.valueOf(10000)))).willReturn(mockWallet);

        mockMvc.perform(post("/api/v1/wallet/123/withdraw").param("amount", "10000"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.balance").value(40000));
    }
}