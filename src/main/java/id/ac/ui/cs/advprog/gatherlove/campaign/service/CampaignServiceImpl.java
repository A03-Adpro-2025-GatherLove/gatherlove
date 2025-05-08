package id.ac.ui.cs.advprog.gatherlove.campaign.service;

import id.ac.ui.cs.advprog.gatherlove.campaign.dto.CampaignDto;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.CampaignStatus;
import id.ac.ui.cs.advprog.gatherlove.campaign.repository.CampaignRepository;
import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import lombok.RequiredArgsConstructor;
// import org.openqa.selenium.NoSuchElementException; //TODO: Sesuaikan rencana anda (Kenapa ada selenium?)
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;

    @Override
    public Campaign createCampaign(CampaignDto dto, UserEntity fundraiser) {
        if (dto.getDeadline() != null && dto.getDeadline().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Deadline harus di masa depan");
        }

        Campaign campaign = Campaign.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .targetAmount(dto.getTargetAmount())
                .deadline(dto.getDeadline())
                .imageUrl(dto.getImageUrl())
                .fundraiser(fundraiser)
                .status(CampaignStatus.PENDING_VERIFICATION)
                .build();

        return campaignRepository.save(campaign);
    }

    @Override
    public List<Campaign> getCampaignsByUser(UserEntity user) {
        return campaignRepository.findByFundraiser(user);
    }

    @Override
    public Campaign getCampaignById(String id) {
        return campaignRepository.findById(id).orElseThrow(); //TODO: Sesuaikan dengan rencana
    }

    @Override
    public Campaign updateCampaign(String id, CampaignDto dto) {
        Campaign campaign = getCampaignById(id);
        campaign.setTitle(dto.getTitle());
        campaign.setDescription(dto.getDescription());
        campaign.setTargetAmount(dto.getTargetAmount());
        campaign.setDeadline(dto.getDeadline());
        campaign.setImageUrl(dto.getImageUrl());
        return campaignRepository.save(campaign);
    }

    @Override
    public void deleteCampaign(String id) {
        campaignRepository.deleteById(id);
    }

    @Override
    public Campaign verifyCampaign(String id, CampaignStatus status) {
        Campaign campaign = getCampaignById(id);
        campaign.setStatus(status);
        return campaignRepository.save(campaign);
    }

    @Override
    public Campaign addDonationToCampaign(String campaignId, BigDecimal amount) {
        Campaign campaign = getCampaignById(campaignId);
        campaign.addDonation(amount);
        return campaignRepository.save(campaign);
    }

    @Override
    public void validateCampaignForDonation(UUID campaignId) {
        // TODO: Sesuaikan rencana dengan DonationService
    }

    @Override
    public void addCollectedAmount(UUID campaignId, BigDecimal amount) {
        // TODO: Sesuaikan rencana dengan DonationService
    }
}
