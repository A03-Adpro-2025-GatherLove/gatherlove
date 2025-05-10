package id.ac.ui.cs.advprog.gatherlove.campaign.repository;

import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.CampaignStatus;
import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CampaignRepository extends JpaRepository<Campaign, String> {
    List<Campaign> findByFundraiser(UserEntity user);
    Optional<Campaign> findById(String id); // TODO: Sesuaikan dengan rencana anda

    List<Campaign> findByStatus(CampaignStatus status); // Izin menambahkan - Yasin
}
