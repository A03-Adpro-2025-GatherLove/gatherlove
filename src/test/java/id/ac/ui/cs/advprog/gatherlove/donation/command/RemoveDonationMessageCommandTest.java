package id.ac.ui.cs.advprog.gatherlove.donation.command;

import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import id.ac.ui.cs.advprog.gatherlove.donation.service.DonationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RemoveDonationMessageCommandTest {

    @Mock
    private DonationService donationService;

    @Test
    void execute_shouldCallServiceRemoveDonationMessage() {
        // Arrange
        UUID donationId = UUID.randomUUID();
        UUID requestingUserId = UUID.randomUUID();
        Donation expectedDonation = Donation.builder().id(donationId).message(null).build(); // Message removed

        when(donationService.removeDonationMessage(donationId, requestingUserId)).thenReturn(expectedDonation);

        Command<Donation> command = new RemoveDonationMessageCommand(donationService, donationId, requestingUserId);

        // Act
        Donation result = command.execute();

        // Assert
        assertThat(result).isEqualTo(expectedDonation);
        verify(donationService, times(1)).removeDonationMessage(donationId, requestingUserId);
    }
}