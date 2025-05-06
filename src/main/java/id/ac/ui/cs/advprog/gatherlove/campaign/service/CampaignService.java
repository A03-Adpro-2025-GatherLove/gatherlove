package id.ac.ui.cs.advprog.gatherlove.campaign.service;

import id.ac.ui.cs.advprog.gatherlove.campaign.dto.CampaignDto;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.CampaignStatus;
import id.ac.ui.cs.advprog.gatherlove.authentication.model.User; // TODO: Saya ubah menjadi User di authentication, sesuaikan dengan rencana anda

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CampaignService {
    Campaign createCampaign(CampaignDto dto, User fundraiser);

    List<Campaign> getCampaignsByUser(User user);

    Campaign getCampaignById(String id);

    Campaign updateCampaign(String id, CampaignDto dto);

    void deleteCampaign(String id);

    /*
     * NOTE FOR ADMIN MODULE:
     * You can inject CampaignService and call:
     * campaignService.verifyCampaign(id, CampaignStatus.APPROVED);
     * or
     * campaignService.verifyCampaign(id, CampaignStatus.REJECTED);
     */
    Campaign verifyCampaign(String id, CampaignStatus status);

    /*
     * NOTE FOR DONATION MODULE:
     * To update donation amount to campaign, call:
     * campaignService.addDonationToCampaign(campaignId, amount);
     */
    Campaign addDonationToCampaign(String campaignId, BigDecimal amount);

    void validateCampaignForDonation(UUID campaignId); // TODO: Sesuaikan rencana dengan DonationService

    void addCollectedAmount(UUID campaignId, BigDecimal amount); // TODO: Sesuaikan rencana dengan DonationService
}
