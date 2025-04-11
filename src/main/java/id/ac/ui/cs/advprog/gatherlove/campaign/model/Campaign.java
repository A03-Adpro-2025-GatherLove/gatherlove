package id.ac.ui.cs.advprog.gatherlove.campaign.model;

import id.ac.ui.cs.advprog.gatherlove.user.model.User;
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

    @Enumerated(EnumType.STRING)
    private CampaignStatus status;

    @ManyToOne
    private User fundraiser;
}
