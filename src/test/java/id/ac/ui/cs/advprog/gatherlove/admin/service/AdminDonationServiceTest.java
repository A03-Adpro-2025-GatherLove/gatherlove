package id.ac.ui.cs.advprog.gatherlove.admin.service;

import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import id.ac.ui.cs.advprog.gatherlove.donation.repository.DonationRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminDonationServiceTest {

    @Mock
    private DonationRepository donationRepository;

    @InjectMocks
    private AdminDonationServiceImpl adminDonationService;

    @Test
    public void testGetDonationHistory() {
        // Arrange
        List<Donation> expectedDonations = new ArrayList<>();
        expectedDonations.add(new Donation());
        expectedDonations.add(new Donation());
        expectedDonations.add(new Donation());

        when(donationRepository.findAll()).thenReturn(expectedDonations);

        // Act
        List<Donation> actualDonations = adminDonationService.getDonationHistory();

        // Assert
        assertEquals(expectedDonations, actualDonations);
    }
}