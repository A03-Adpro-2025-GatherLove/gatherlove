package id.ac.ui.cs.advprog.gatherlove.admin.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CampaignResponse {
    private String campaignId;
    private String title;
    private String description;
    private String status;
    private LocalDate deadline;
    private LocalDateTime createdAt;
    
    public static CampaignResponse from(Campaign campaign) {
        CampaignResponse response = new CampaignResponse();
        response.setCampaignId(campaign.getId());
        response.setTitle(campaign.getTitle());
        response.setDescription(campaign.getDescription());
        response.setStatus(campaign.getStatus().toString());
        response.setDeadline(campaign.getDeadline());
        response.setCreatedAt(campaign.getCreatedAt());
        return response;
    }
}
