package id.ac.ui.cs.advprog.gatherlove.campaign.repository;

import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
import id.ac.ui.cs.advprog.gatherlove.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign, String> {
    List<Campaign> findByFundraiser(User user);
    Optional<Campaign> findById(String id);
}
