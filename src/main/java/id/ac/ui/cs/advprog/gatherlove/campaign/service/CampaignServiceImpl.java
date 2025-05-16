package id.ac.ui.cs.advprog.gatherlove.campaign.service;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import id.ac.ui.cs.advprog.gatherlove.campaign.dto.CampaignDto;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.CampaignStatus;
import id.ac.ui.cs.advprog.gatherlove.campaign.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
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
        return campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign tidak ditemukan dengan ID: " + id));
    }

    @Override
    public Campaign updateCampaign(String id, CampaignDto dto) {
        Campaign campaign = getCampaignById(id);
        
        if (!campaign.canEdit()) {
            throw new IllegalStateException("Kampanye tidak dapat diubah dalam status " + campaign.getStatus());
        }
        
        campaign.setTitle(dto.getTitle());
        campaign.setDescription(dto.getDescription());
        campaign.setTargetAmount(dto.getTargetAmount());
        campaign.setDeadline(dto.getDeadline());
        campaign.setImageUrl(dto.getImageUrl());
        
        return campaignRepository.save(campaign);
    }

    @Override
    public void deleteCampaign(String id) {
        Campaign campaign = getCampaignById(id);
        
        if (!campaign.canDelete()) {
            throw new IllegalStateException("Kampanye tidak dapat dihapus dalam status " + campaign.getStatus());
        }
        
        campaignRepository.deleteById(id);
    }

    @Override
    public Campaign verifyCampaign(String id, boolean approve) {
        Campaign campaign = getCampaignById(id);
        campaign.verify(approve);
        return campaignRepository.save(campaign);
    }

    @Override
    public Campaign addDonationToCampaign(String campaignId, BigDecimal amount) {
        Campaign campaign = getCampaignById(campaignId);
        campaign.addDonation(amount);
        // Check if the donation changed the campaign status (e.g., reached the target)
        campaign.checkStatus();
        return campaignRepository.save(campaign);
    }

    @Override
    public List<Campaign> getCampaignsByStatus(CampaignStatus status) {
        return campaignRepository.findByStatus(status);
    }
    
    @Override
    public boolean canEditCampaign(String id) {
        Campaign campaign = getCampaignById(id);
        return campaign.canEdit();
    }
    
    @Override
    public boolean canDeleteCampaign(String id) {
        Campaign campaign = getCampaignById(id);
        return campaign.canDelete();
    }
    
    @Override
    public Campaign updateCampaignStatus(String id) {
        Campaign campaign = getCampaignById(id);
        campaign.checkStatus();
        return campaignRepository.save(campaign);
    }

    @Override
    public void validateCampaignForDonation(UUID campaignId) {
        Campaign campaign = getCampaignById(campaignId.toString());
        if (campaign.getStatus() != CampaignStatus.APPROVED) {
            throw new IllegalStateException("Cannot donate to campaign with status: " + campaign.getStatus());
        }
    }

    @Override
    public void addCollectedAmount(UUID campaignId, BigDecimal amount) {
        Campaign campaign = getCampaignById(campaignId.toString());
        campaign.addDonation(amount); // This will use your State pattern
        campaignRepository.save(campaign);
    }
}