package id.ac.ui.cs.advprog.gatherlove.donation.service;

import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import id.ac.ui.cs.advprog.gatherlove.donation.repository.DonationRepository;
import id.ac.ui.cs.advprog.gatherlove.wallet.service.WalletService;
import id.ac.ui.cs.advprog.gatherlove.campaign.service.CampaignService;
import id.ac.ui.cs.advprog.gatherlove.donation.exception.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class DonationServiceImplTest {

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private WalletService walletService; // Mock external dependency

    @Mock
    private CampaignService campaignService; // Mock external dependency

    @InjectMocks
    private DonationServiceImpl donationService;

    private UUID donorId;
    private UUID campaignId;
    private BigDecimal amount;
    private String message;
    private Donation donation;

    @BeforeEach
    void setUp() {
        donorId = UUID.randomUUID();
        campaignId = UUID.randomUUID();
        amount = new BigDecimal("100.00");
        message = "Test message";
        donation = Donation.builder()
                .id(UUID.randomUUID())
                .donorId(donorId)
                .campaignId(campaignId)
                .amount(amount)
                .message(message)
                .donationTimestamp(LocalDateTime.now())
                .build();
    }

    @Test
    void createDonation_whenValid_shouldSaveAndReturnDonation() {
        // Arrange
        // Assume campaignService validation passes (or mock it)
        // doNothing().when(campaignService).validateCampaignForDonation(campaignId);
        // Assume walletService debit succeeds
        // doNothing().when(walletService).debit(donorId, amount);
        when(donationRepository.save(any(Donation.class))).thenAnswer(invocation -> {
            Donation d = invocation.getArgument(0);
            d.setId(UUID.randomUUID()); // Simulate saving and getting an ID
            return d;
        });
        // Assume campaignService update amount succeeds
        // doNothing().when(campaignService).addCollectedAmount(campaignId, amount);


        // Act
        Donation createdDonation = donationService.createDonation(donorId, campaignId, amount, message);

        // Assert
        assertThat(createdDonation).isNotNull();
        assertThat(createdDonation.getDonorId()).isEqualTo(donorId);
        assertThat(createdDonation.getCampaignId()).isEqualTo(campaignId);
        assertThat(createdDonation.getAmount()).isEqualByComparingTo(amount);
        assertThat(createdDonation.getMessage()).isEqualTo(message);

        verify(campaignService, times(1)).validateCampaignForDonation(campaignId); // Verify validation called
        verify(walletService, times(1)).debit(donorId, amount); // Verify wallet debit
        verify(donationRepository, times(1)).save(any(Donation.class)); // Verify save called
        verify(campaignService, times(1)).addCollectedAmount(campaignId, amount); // Verify campaign update
    }

    @Test
    void createDonation_whenAmountNotPositive_shouldThrowException() {
        // Arrange
        BigDecimal zeroAmount = BigDecimal.ZERO;
        BigDecimal negativeAmount = new BigDecimal("-10.00");

        // Act & Assert for Zero
        assertThatThrownBy(() -> donationService.createDonation(donorId, campaignId, zeroAmount, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Donation amount must be positive.");

        // Act & Assert for Negative
        assertThatThrownBy(() -> donationService.createDonation(donorId, campaignId, negativeAmount, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Donation amount must be positive.");

        verify(donationRepository, never()).save(any(Donation.class)); // Ensure save wasn't called
        verify(walletService, never()).debit(any(UUID.class), any(BigDecimal.class)); // Ensure debit wasn't called
    }

}