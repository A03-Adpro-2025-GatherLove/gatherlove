package id.ac.ui.cs.advprog.gatherlove.donation.repository;

import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Use configured DB (e.g., H2 or Postgres)
class DonationRepositoryTest {

    @Autowired
    private DonationRepository donationRepository;

    @Test
    void whenSaveAndFindById_thenCorrect() {
        UUID campaignId = UUID.randomUUID();
        UUID donorId = UUID.randomUUID();
        Donation donation = Donation.builder()
                .campaignId(campaignId)
                .donorId(donorId)
                .amount(new BigDecimal("50.00"))
                .message("Test donation")
                .build();

        Donation savedDonation = donationRepository.save(donation);
        Donation foundDonation = donationRepository.findById(savedDonation.getId()).orElse(null);

        assertThat(foundDonation).isNotNull();
        assertThat(foundDonation.getId()).isEqualTo(savedDonation.getId());
        assertThat(foundDonation.getCampaignId()).isEqualTo(campaignId);
        assertThat(foundDonation.getDonorId()).isEqualTo(donorId);
        assertThat(foundDonation.getAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
    }
}