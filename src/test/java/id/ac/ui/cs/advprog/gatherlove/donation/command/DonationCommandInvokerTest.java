package id.ac.ui.cs.advprog.gatherlove.donation.command;

import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger; // Import manual diperlukan jika ingin mock static field seperti logger

import java.util.concurrent.CompletableFuture;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DonationCommandInvokerTest {

    @Mock
    private Command<Donation> mockCommand; // Gunakan tipe generik Command<Donation> atau Command<?>

    @InjectMocks
    private DonationCommandInvoker donationCommandInvoker;

    private Donation mockDonationResult;

    @BeforeEach
    void setUp() {
        mockDonationResult = new Donation(); // Buat objek dummy
    }

    @Test
    void executeCommand_shouldCallExecuteOnCommand() {
        // Arrange
        when(mockCommand.execute()).thenReturn(CompletableFuture.completedFuture(mockDonationResult));

        // Act
        CompletableFuture<Donation> futureResult = donationCommandInvoker.executeCommand(mockCommand);
        Donation result = futureResult.join();

        // Assert
        verify(mockCommand, times(1)).execute();
        assertNotNull(result);
        assertEquals(mockDonationResult, result);
    }

    @Test
    void executeCommand_whenCommandFails_shouldCompleteExceptionally() {
        // Arrange
        RuntimeException testException = new RuntimeException("Command execution failed");
        when(mockCommand.execute()).thenReturn(CompletableFuture.failedFuture(testException));

        // Act
        CompletableFuture<Donation> futureResult = donationCommandInvoker.executeCommand(mockCommand);

        // Assert
        assertTrue(futureResult.isCompletedExceptionally());
        try {
            futureResult.join();
            fail("Should have thrown an exception");
        } catch (Exception e) {
            assertEquals(testException, e.getCause());
        }
        verify(mockCommand, times(1)).execute();
    }
}