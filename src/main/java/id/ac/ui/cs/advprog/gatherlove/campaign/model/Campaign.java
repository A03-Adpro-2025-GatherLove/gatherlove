package id.ac.ui.cs.advprog.gatherlove.campaign.model;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;
    private String description;
    private BigDecimal targetAmount;
    private LocalDate deadline;
    private String imageUrl;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private CampaignStatus status = CampaignStatus.PENDING_VERIFICATION;

    @ManyToOne
    private User fundraiser;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal totalDonated = BigDecimal.ZERO;

    public void addDonation(BigDecimal amount) {
        this.totalDonated = this.totalDonated.add(amount);
    }
}
