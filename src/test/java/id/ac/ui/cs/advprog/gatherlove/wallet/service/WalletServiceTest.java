package id.ac.ui.cs.advprog.gatherlove.wallet.service;

import id.ac.ui.cs.advprog.gatherlove.wallet.model.*;
import id.ac.ui.cs.advprog.gatherlove.wallet.enums.TransactionType;
import id.ac.ui.cs.advprog.gatherlove.wallet.repository.*;
import id.ac.ui.cs.advprog.gatherlove.wallet.strategy.PaymentStrategy;
import id.ac.ui.cs.advprog.gatherlove.wallet.observer.WalletEventPublisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    @Mock
    private WalletEventPublisher walletEventPublisher;

    @Mock
    private PaymentStrategy goPayStrategy;

    @Mock
    private PaymentStrategy danaStrategy;

    private Wallet exampleWallet;

    @BeforeEach
    void setup() {
        exampleWallet = new Wallet(123L, BigDecimal.valueOf(80000));
        when(walletRepository.findByUserId(123L)).thenReturn(Optional.of(exampleWallet));
    }

    @Test
    void testTopUpSuccess() {
        when(goPayStrategy.pay(eq(BigDecimal.valueOf(10000)), eq("080808080808"))).thenReturn(true);
        Wallet updatedWallet = walletService.topUp(123L, BigDecimal.valueOf(10000), "080808080808", "GOPAY");
        assertEquals(BigDecimal.valueOf(90000), updatedWallet.getBalance());
        verify(goPayStrategy).pay(BigDecimal.valueOf(10000), "080808080808");
        verify(walletEventPublisher).notifyBalanceChanged(any(Wallet.class), any(Transaction.class));
    }

    @Test
    void testTopUpFailure() {
        assertThrows(RuntimeException.class, () -> {
            walletService.topUp(123L, BigDecimal.valueOf(10000), "080808080808", "DANA");
        });
        verify(walletEventPublisher, never()).notifyBalanceChanged(any(), any());
    }

    @Test
    void testGetWalletBalance() {
        BigDecimal balance = walletService.getWalletBalance(123L);
        assertEquals(BigDecimal.valueOf(80000), balance);
        verify(walletRepository).findByUserId(123L);
    }

    @Test
    void testDeleteTopUpTransaction() {
        Transaction tr = new Transaction(TransactionType.TOP_UP, BigDecimal.valueOf(1000), "GOPAY");
        tr.setId(998L);
        tr.setWallet(exampleWallet);
        when(transactionRepository.findById(998L)).thenReturn(Optional.of(tr));
        walletService.deleteTopUpTransaction(123L, 998L);
        assertEquals(BigDecimal.valueOf(79000), exampleWallet.getBalance());
        verify(transactionRepository).delete(tr);
        verify(walletEventPublisher).notifyBalanceChanged(exampleWallet, tr);
    }

    @Test
    void testDeleteTopUpTransactionInvalidType() {
        Transaction tr = new Transaction(TransactionType.DONATION, BigDecimal.valueOf(2000), "GOPAY");
        tr.setId(999L);
        tr.setWallet(exampleWallet);
        when(transactionRepository.findById(999L)).thenReturn(Optional.of(tr));
        assertThrows(RuntimeException.class, () -> {
            walletService.deleteTopUpTransaction(123L, 999L);
        });
        verify(transactionRepository, never()).delete(any());
        verify(walletEventPublisher, never()).notifyBalanceChanged(any(), any());
    }

    @Test
    void testWithdrawFundsInsufficientBalance() {
        assertThrows(RuntimeException.class, () -> {
            walletService.withdrawFunds(123L, BigDecimal.valueOf(99999999));
        });
        verify(walletEventPublisher, never()).notifyBalanceChanged(any(), any());
    }

    @Test
    void testWithdrawFundsSuccess() {
        Wallet updated = walletService.withdrawFunds(123L, BigDecimal.valueOf(10000));
        assertEquals(BigDecimal.valueOf(70000), updated.getBalance());
        verify(walletEventPublisher).notifyBalanceChanged(any(Wallet.class), any(Transaction.class));
    }
}