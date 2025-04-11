package id.ac.ui.cs.advprog.gatherlove.campaign.repository;

import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<Campaign, String> {
}
