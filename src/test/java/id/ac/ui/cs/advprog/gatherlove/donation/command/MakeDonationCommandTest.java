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
class MakeDonationCommandTest {

    @Mock
    private DonationService donationService;

    private MakeDonationCommand makeDonationCommand;
    private UUID donorId;
    private String campaignId;
    private BigDecimal amount;
    private String message;
    private Donation expectedDonation;

    @BeforeEach
    void setUp() {
        donorId = UUID.randomUUID();
        campaignId = "campaign-123";
        amount = new BigDecimal("100.00");
        message = "Test donation message";

        expectedDonation = Donation.builder()
                .id(UUID.randomUUID())
                .donorId(donorId)
                .campaignId(campaignId)
                .amount(amount)
                .message(message)
                .donationTimestamp(LocalDateTime.now())
                .build();

        makeDonationCommand = new MakeDonationCommand(donationService, donorId, campaignId, amount, message);
    }

    @Test
    void execute_shouldCallCreateDonationOnService() {
        // Arrange
        when(donationService.createDonation(donorId, campaignId, amount, message))
                .thenReturn(CompletableFuture.completedFuture(expectedDonation));

        // Act
        CompletableFuture<Donation> futureResult = makeDonationCommand.execute();
        Donation result = futureResult.join(); // Tunggu hasil untuk assertion

        // Assert
        verify(donationService, times(1)).createDonation(donorId, campaignId, amount, message);
        assertNotNull(result);
        assertEquals(expectedDonation, result);
    }
}