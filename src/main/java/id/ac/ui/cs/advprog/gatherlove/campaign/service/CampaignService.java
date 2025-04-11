package id.ac.ui.cs.advprog.gatherlove.campaign.service;

import id.ac.ui.cs.advprog.gatherlove.campaign.dto.CampaignDto;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.CampaignStatus;
import id.ac.ui.cs.advprog.gatherlove.user.model.User;

import java.util.List;

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
}
