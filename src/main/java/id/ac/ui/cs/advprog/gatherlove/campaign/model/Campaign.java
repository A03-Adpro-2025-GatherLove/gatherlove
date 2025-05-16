package id.ac.ui.cs.advprog.gatherlove.campaign.model;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import id.ac.ui.cs.advprog.gatherlove.campaign.state.CampaignState;
import id.ac.ui.cs.advprog.gatherlove.campaign.state.CampaignStateFactory;
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
    private UserEntity fundraiser;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal totalDonated = BigDecimal.ZERO;

    public void addDonation(BigDecimal amount) {
        CampaignState state = CampaignStateFactory.getState(this.status);
        state.donate(this, amount);
    }
    
    @Transient
    public CampaignState getState() {
        return CampaignStateFactory.getState(this.status);
    }
    
    public boolean canEdit() {
        return getState().canEdit(this);
    }
    
    public boolean canDelete() {
        return getState().canDelete(this);
    }
    
    public Campaign verify(boolean approve) {
        return getState().verify(this, approve);
    }
    
    public Campaign checkStatus() {
        return getState().checkStatus(this);
    }
}