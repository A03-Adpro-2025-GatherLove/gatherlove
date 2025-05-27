package id.ac.ui.cs.advprog.gatherlove.donation.service;

import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
import id.ac.ui.cs.advprog.gatherlove.campaign.service.CampaignService;
import id.ac.ui.cs.advprog.gatherlove.donation.exception.DonationNotFoundException;
import id.ac.ui.cs.advprog.gatherlove.donation.exception.UnauthorizedException;
import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import id.ac.ui.cs.advprog.gatherlove.donation.repository.DonationRepository;
import id.ac.ui.cs.advprog.gatherlove.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DonationServiceImplTest {

    @Mock
    private DonationRepository donationRepository;
    @Mock
    private WalletService walletService;
    @Mock
    private CampaignService campaignService;

    @InjectMocks
    private DonationServiceImpl donationService;

    private UUID donorId;
    private String campaignIdStr;
    private UUID campaignIdUUID;
    private BigDecimal amount;
    private String message;
    private Donation donation;
    private Campaign mockCampaign;

    @BeforeEach
    void setUp() {
        donorId = UUID.randomUUID();
        campaignIdUUID = UUID.randomUUID();
        campaignIdStr = campaignIdUUID.toString();
        amount = new BigDecimal("200.00");
        message = "A generous donation";

        donation = Donation.builder()
                .id(UUID.randomUUID())
                .donorId(donorId)
                .campaignId(campaignIdStr)
                .amount(amount)
                .message(message)
                .donationTimestamp(LocalDateTime.now())
                .build();

        mockCampaign = new Campaign();
        mockCampaign.setId(campaignIdStr);
        mockCampaign.setTitle("Test Campaign Title");
    }

    @Test
    void createDonation_successful() throws ExecutionException, InterruptedException {
        // Arrange
        when(campaignService.getCampaignById(campaignIdStr)).thenReturn(mockCampaign); // Untuk getTitle
        when(donationRepository.save(any(Donation.class))).thenReturn(donation);
        // Tidak perlu mock validateCampaignForDonation, getOrCreateWallet, debit, addDonationToCampaign jika mereka void
        // atau jika Anda tidak memeriksa interaksinya secara spesifik dan hanya fokus pada hasil save.

        // Act
        CompletableFuture<Donation> futureResult = donationService.createDonation(donorId, campaignIdStr, amount, message);
        Donation result = futureResult.get(); // .get() untuk test, di production gunakan thenApply, etc.

        // Assert
        assertNotNull(result);
        assertEquals(donorId, result.getDonorId());
        assertEquals(campaignIdStr, result.getCampaignId());
        assertEquals(0, amount.compareTo(result.getAmount())); // BigDecimal comparison
        assertEquals(message, result.getMessage());

        verify(campaignService, times(1)).validateCampaignForDonation(campaignIdUUID);
        verify(walletService, times(1)).getOrCreateWallet(donorId);
        verify(walletService, times(1)).debit(eq(donorId), eq(amount), any(UUID.class), eq(mockCampaign.getTitle()));
        verify(donationRepository, times(1)).save(any(Donation.class));
        verify(campaignService, times(1)).addDonationToCampaign(campaignIdStr, amount);
    }

    @Test
    void createDonation_invalidAmount_throwsIllegalArgumentException() {
        // Arrange
        BigDecimal invalidAmount = new BigDecimal("-10.00");

        // Act & Assert
        // Karena createDonation @Async, exception akan di-wrap dalam ExecutionException jika kita panggil .get()
        // Cara yang lebih baik adalah memeriksa apakah CompletableFuture selesai secara exceptional
        CompletableFuture<Donation> future = donationService.createDonation(donorId, campaignIdStr, invalidAmount, message);

        assertThrows(ExecutionException.class, () -> {
            try {
                future.get();
            } catch (ExecutionException e) {
                assertTrue(e.getCause() instanceof IllegalArgumentException);
                assertEquals("Donation amount must be positive.", e.getCause().getMessage());
                throw e; // Re-throw untuk memenuhi assertThrows
            }
        });

        verify(donationRepository, never()).save(any(Donation.class));
    }


    @Test
    void removeDonationMessage_successful() throws ExecutionException, InterruptedException {
        // Arrange
        UUID donationIdToRemove = donation.getId();
        when(donationRepository.findById(donationIdToRemove)).thenReturn(Optional.of(donation));
        when(donationRepository.save(any(Donation.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Return saved entity

        // Act
        CompletableFuture<Donation> futureResult = donationService.removeDonationMessage(donationIdToRemove, donorId);
        Donation result = futureResult.get();

        // Assert
        assertNotNull(result);
        assertNull(result.getMessage());
        verify(donationRepository, times(1)).findById(donationIdToRemove);
        verify(donationRepository, times(1)).save(donation);
    }

    @Test
    void removeDonationMessage_donationNotFound_throwsDonationNotFoundException() {
        // Arrange
        UUID nonExistentDonationId = UUID.randomUUID();
        when(donationRepository.findById(nonExistentDonationId)).thenReturn(Optional.empty());

        // Act & Assert
        CompletableFuture<Donation> future = donationService.removeDonationMessage(nonExistentDonationId, donorId);
        assertThrows(ExecutionException.class, () -> {
            try {
                future.get();
            } catch (ExecutionException e) {
                assertTrue(e.getCause() instanceof DonationNotFoundException);
                throw e;
            }
        });
        verify(donationRepository, never()).save(any(Donation.class));
    }

    @Test
    void removeDonationMessage_unauthorizedUser_throwsUnauthorizedException() {
        // Arrange
        UUID donationIdToRemove = donation.getId();
        UUID otherUserId = UUID.randomUUID();
        when(donationRepository.findById(donationIdToRemove)).thenReturn(Optional.of(donation)); // donation.getDonorId() adalah donorId

        // Act & Assert
        CompletableFuture<Donation> future = donationService.removeDonationMessage(donationIdToRemove, otherUserId);

        assertThrows(ExecutionException.class, () -> {
            try {
                future.get();
            } catch (ExecutionException e) {
                assertTrue(e.getCause() instanceof UnauthorizedException);
                throw e;
            }
        });
        verify(donationRepository, never()).save(any(Donation.class));
    }

    @Test
    void findDonationById_found() {
        when(donationRepository.findById(donation.getId())).thenReturn(Optional.of(donation));
        Donation found = donationService.findDonationById(donation.getId());
        assertNotNull(found);
        assertEquals(donation.getId(), found.getId());
    }

    @Test
    void findDonationById_notFound_throwsDonationNotFoundException() {
        UUID id = UUID.randomUUID();
        when(donationRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(DonationNotFoundException.class, () -> donationService.findDonationById(id));
    }

    @Test
    void findDonationsByDonor_returnsList() {
        when(donationRepository.findByDonorIdOrderByDonationTimestampDesc(donorId)).thenReturn(Arrays.asList(donation));
        List<Donation> donations = donationService.findDonationsByDonor(donorId);
        assertFalse(donations.isEmpty());
        assertEquals(1, donations.size());
        assertEquals(donation, donations.get(0));
    }

    @Test
    void findDonationsByCampaign_returnsList() {
        when(donationRepository.findByCampaignIdOrderByDonationTimestampDesc(campaignIdStr)).thenReturn(Arrays.asList(donation));
        List<Donation> donations = donationService.findDonationsByCampaign(campaignIdStr);
        assertFalse(donations.isEmpty());
        assertEquals(1, donations.size());
        assertEquals(donation, donations.get(0));
    }

    @Test
    void findAllDonations_returnsList() {
        when(donationRepository.findAll()).thenReturn(Arrays.asList(donation));
        List<Donation> donations = donationService.findAllDonations();
        assertFalse(donations.isEmpty());
        assertEquals(1, donations.size());
    }
}