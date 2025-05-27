package id.ac.ui.cs.advprog.gatherlove.campaign.service;

import id.ac.ui.cs.advprog.gatherlove.campaign.dto.CampaignDto;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
import id.ac.ui.cs.advprog.gatherlove.campaign.repository.CampaignRepository;
import id.ac.ui.cs.advprog.gatherlove.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CampaignServiceTest {

    private CampaignService campaignService;
    private CampaignRepository campaignRepository;

    @BeforeEach
    void setUp() {
        campaignRepository = mock(CampaignRepository.class);
        campaignService = new CampaignServiceImpl(campaignRepository);
    }

    @Test
    void testCreateCampaign_WithValidInput_ShouldSaveCampaignToRepository() {
        // Arrange
        CampaignDto dto = new CampaignDto();
        dto.setTitle("Bantu Sekolah Anak");
        dto.setDescription("Kampanye pendidikan untuk anak-anak daerah terpencil");
        dto.setTargetAmount(BigDecimal.valueOf(100_000));
        dto.setDeadline(LocalDate.now().plusDays(10));
        dto.setImageUrl("https://example.com/image.jpg");

        User fundraiser = new User();
        fundraiser.setUsername("fundraiser123");

        // Act
        campaignService.createCampaign(dto, fundraiser);

        // Assert
        ArgumentCaptor<Campaign> captor = ArgumentCaptor.forClass(Campaign.class);
        verify(campaignRepository, times(1)).save(captor.capture());

        Campaign saved = captor.getValue();
        assertThat(saved.getTitle()).isEqualTo(dto.getTitle());
        assertThat(saved.getFundraiser()).isEqualTo(fundraiser);
        assertThat(saved.getTargetAmount()).isEqualTo(dto.getTargetAmount());
    }
}
