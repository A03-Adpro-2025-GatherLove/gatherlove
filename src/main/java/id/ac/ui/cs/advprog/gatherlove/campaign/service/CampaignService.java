package id.ac.ui.cs.advprog.gatherlove.campaign.service;

import id.ac.ui.cs.advprog.gatherlove.campaign.dto.CampaignDto;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
import id.ac.ui.cs.advprog.gatherlove.user.model.User;

import java.util.List;

public interface CampaignService {
    Campaign createCampaign(CampaignDto dto, User fundraiser);

    List<Campaign> getCampaignsByUser(User user);
}
