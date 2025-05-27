package id.ac.ui.cs.advprog.gatherlove.donation.command;

import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import id.ac.ui.cs.advprog.gatherlove.donation.service.DonationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RemoveDonationMessageCommandTest {

    @Mock
    private DonationService donationService;

    private RemoveDonationMessageCommand removeDonationMessageCommand;
    private UUID donationId;
    private UUID requestingUserId;
    private Donation expectedDonation;

    @BeforeEach
    void setUp() {
        donationId = UUID.randomUUID();
        requestingUserId = UUID.randomUUID();

        expectedDonation = Donation.builder()
                .id(donationId)
                .donorId(requestingUserId)
                .campaignId("campaign-xyz")
                .amount(new BigDecimal("50.00"))
                .message(null) // Pesan akan jadi null setelah dihapus
                .donationTimestamp(LocalDateTime.now())
                .build();

        removeDonationMessageCommand = new RemoveDonationMessageCommand(donationService, donationId, requestingUserId);
    }

    @Test
    void execute_shouldCallRemoveDonationMessageOnService() {
        // Arrange
        when(donationService.removeDonationMessage(donationId, requestingUserId))
                .thenReturn(CompletableFuture.completedFuture(expectedDonation));

        // Act
        CompletableFuture<Donation> futureResult = removeDonationMessageCommand.execute();
        Donation result = futureResult.join();

        // Assert
        verify(donationService, times(1)).removeDonationMessage(donationId, requestingUserId);
        assertNotNull(result);
        assertEquals(expectedDonation, result);
        assertNull(result.getMessage());
    }
}