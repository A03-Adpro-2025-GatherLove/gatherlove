package id.ac.ui.cs.advprog.gatherlove.donation.command;

import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import id.ac.ui.cs.advprog.gatherlove.donation.service.DonationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class MakeDonationCommandTest {

    @Mock
    private DonationService donationService;

    @Test
    void execute_shouldCallServiceCreateDonation() {
        // Arrange
        UUID donorId = UUID.randomUUID();
        UUID campaignId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("50.00");
        String message = "Go team!";
        Donation expectedDonation = Donation.builder().id(UUID.randomUUID()).build();

        when(donationService.createDonation(donorId, campaignId, amount, message)).thenReturn(expectedDonation);

        Command<Donation> command = new MakeDonationCommand(donationService, donorId, campaignId, amount, message);

        // Act
        Donation result = command.execute();

        // Assert
        assertThat(result).isEqualTo(expectedDonation);
        verify(donationService, times(1)).createDonation(donorId, campaignId, amount, message);
    }
}