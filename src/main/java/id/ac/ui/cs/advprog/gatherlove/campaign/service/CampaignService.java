package id.ac.ui.cs.advprog.gatherlove.campaign.service;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import id.ac.ui.cs.advprog.gatherlove.campaign.dto.CampaignDto;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.CampaignStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CampaignService {
    Campaign createCampaign(CampaignDto dto, UserEntity fundraiser);
    List<Campaign> getCampaignsByUser(UserEntity user);
    Campaign getCampaignById(String id);
    Campaign updateCampaign(String id, CampaignDto dto);
    void deleteCampaign(String id);
    Campaign verifyCampaign(String id, boolean approve);
    Campaign addDonationToCampaign(String campaignId, BigDecimal amount);
    List<Campaign> getCampaignsByStatus(CampaignStatus status);
    boolean canEditCampaign(String id);
    boolean canDeleteCampaign(String id);
    Campaign updateCampaignStatus(String id);
    void validateCampaignForDonation(UUID campaignId);
    void addCollectedAmount(UUID campaignId, BigDecimal amount);
}
