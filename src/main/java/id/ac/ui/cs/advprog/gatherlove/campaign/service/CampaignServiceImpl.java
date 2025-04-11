package id.ac.ui.cs.advprog.gatherlove.campaign.service;

import id.ac.ui.cs.advprog.gatherlove.campaign.dto.CampaignDto;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.CampaignStatus;
import id.ac.ui.cs.advprog.gatherlove.campaign.repository.CampaignRepository;
import id.ac.ui.cs.advprog.gatherlove.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;

    @Override
    public Campaign createCampaign(CampaignDto dto, User fundraiser) {
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
    public List<Campaign> getCampaignsByUser(User user) {
        return campaignRepository.findByFundraiser(user);
    }
}
