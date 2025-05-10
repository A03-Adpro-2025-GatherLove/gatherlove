package id.ac.ui.cs.advprog.gatherlove.admin.service;

import id.ac.ui.cs.advprog.gatherlove.admin.dto.Stats;
import id.ac.ui.cs.advprog.gatherlove.campaign.repository.CampaignRepository;
import id.ac.ui.cs.advprog.gatherlove.donation.repository.DonationRepository;
import id.ac.ui.cs.advprog.gatherlove.authentication.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
public class AdminDashboardServiceTest {
    
    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminDashboardServiceImpl adminDashboardService;

    @Test
    public void testGetStats() {
        when(campaignRepository.count()).thenReturn(10L);
        when(donationRepository.count()).thenReturn(20L);
        when(userRepository.count()).thenReturn(30L);
        when(donationRepository.getTotalFundRaised()).thenReturn(BigDecimal.valueOf(1000.0));

        Stats stats = adminDashboardService.getStats();

        assertEquals(10, stats.getTotalCampaigns());
        assertEquals(20, stats.getTotalDonations());
        assertEquals(30, stats.getTotalUsers());
        assertEquals(BigDecimal.valueOf(1000.0), stats.getTotalFundRaised());
    }
}
