package id.ac.ui.cs.advprog.gatherlove.campaign.dto;

import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
import lombok.NoArgsConstructor;
// import lombok.Setter;
// import lombok.Getter;

@NoArgsConstructor
public class CampaignDtoFactory {
    
    public static CampaignDto createFromEntity(Campaign campaign) {
        CampaignDto dto = new CampaignDto();
        dto.setTitle(campaign.getTitle());
        dto.setDescription(campaign.getDescription());
        dto.setDeadline(campaign.getDeadline());
        dto.setImageUrl(campaign.getImageUrl());
        dto.setTargetAmount(campaign.getTargetAmount());
        return dto;
    }
}