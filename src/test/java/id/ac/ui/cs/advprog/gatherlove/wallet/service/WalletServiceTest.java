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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private WalletServiceImpl walletService;

    @Mock
    private WalletEventPublisher walletEventPublisher;

    @Mock
    private PaymentStrategy goPayStrategy;

    @Mock
    private PaymentStrategy danaStrategy;

    private Wallet exampleWallet;

    private final UUID userID = UUID.randomUUID();

    @BeforeEach
    void setup() {
        exampleWallet = new Wallet(userID, BigDecimal.valueOf(80000));
        when(walletRepository.findByUserId(userID)).thenReturn(Optional.of(exampleWallet));

        Map<String, PaymentStrategy> strategyMap = Map.of(
                "gopay", goPayStrategy, "dana", danaStrategy);

        walletService = new WalletServiceImpl(walletRepository, transactionRepository,
                walletEventPublisher, strategyMap);
    }

    @Test
    void testTopUpSuccess() {
        when(goPayStrategy.pay(eq(BigDecimal.valueOf(10000)), eq("080808080808"))).thenReturn(true);
        Wallet updatedWallet = walletService.topUp(userID, BigDecimal.valueOf(10000), "080808080808", "GOPAY");
        assertEquals(BigDecimal.valueOf(90000), updatedWallet.getBalance());
        verify(goPayStrategy).pay(BigDecimal.valueOf(10000), "080808080808");
        verify(walletEventPublisher).notifyBalanceChanged(any(Wallet.class), any(Transaction.class));
    }

    @Test
    void testTopUpFailure() {
        when(danaStrategy.pay(eq(BigDecimal.valueOf(10000)), eq("080808080808"))).thenReturn(false);
        assertThrows(RuntimeException.class, () -> {
            walletService.topUp(userID, BigDecimal.valueOf(10000), "080808080808", "DANA");
        });
        verify(walletEventPublisher, never()).notifyBalanceChanged(any(), any());
    }

    @Test
    void testGetWalletBalance() {
        BigDecimal balance = walletService.getWalletBalance(userID);
        assertEquals(BigDecimal.valueOf(80000), balance);
        verify(walletRepository).findByUserId(userID);
    }

    @Test
    void testDeleteTopUpTransaction() {
        Transaction tr = new Transaction(TransactionType.TOP_UP, BigDecimal.valueOf(1000), "GOPAY");
        tr.setId(998L);
        tr.setWallet(exampleWallet);
        when(transactionRepository.findById(998L)).thenReturn(Optional.of(tr));
        walletService.deleteTopUpTransaction(userID, 998L);
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
            walletService.deleteTopUpTransaction(userID, 999L);
        });
        verify(transactionRepository, never()).delete(any());
        verify(walletEventPublisher, never()).notifyBalanceChanged(any(), any());
    }

    @Test
    void testWithdrawFundsInsufficientBalance() {
        assertThrows(RuntimeException.class, () -> {
            walletService.withdrawFunds(userID, BigDecimal.valueOf(99999999));
        });
        verify(walletEventPublisher, never()).notifyBalanceChanged(any(), any());
    }

    @Test
    void testWithdrawFundsSuccess() {
        Wallet updated = walletService.withdrawFunds(userID, BigDecimal.valueOf(10000));
        assertEquals(BigDecimal.valueOf(70000), updated.getBalance());
        verify(walletEventPublisher).notifyBalanceChanged(any(Wallet.class), any(Transaction.class));
    }

    @Test
    void testDonateSuccess() {
        Wallet updatedWallet = walletService.debit(userID, BigDecimal.valueOf(15000));
        assertEquals(BigDecimal.valueOf(65000), updatedWallet.getBalance());
        assertTrue(updatedWallet.getTransactions().stream().anyMatch(
                t -> t.getType() == TransactionType.DONATION &&
                        t.getAmount().compareTo(BigDecimal.valueOf(15000)) == 0));

        verify(transactionRepository).save(any(Transaction.class));
        verify(walletRepository).save(updatedWallet);
        verify(walletEventPublisher).notifyBalanceChanged(updatedWallet,
                updatedWallet.getTransactions().getLast());
    }

    @Test
    void testDonateInsufficientBalance() {
        assertThrows(RuntimeException.class, () -> walletService.debit(userID, BigDecimal.valueOf(1000000)));
        assertEquals(BigDecimal.valueOf(80000), exampleWallet.getBalance());
        verify(walletEventPublisher, never()).notifyBalanceChanged(any(), any());
    }
}