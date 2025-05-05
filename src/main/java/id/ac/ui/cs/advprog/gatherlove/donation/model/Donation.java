package id.ac.ui.cs.advprog.gatherlove.donation.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "donations")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Donation {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private UUID campaignId;
    @Column(nullable = false) private UUID donorId;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal amount;
    @Column(nullable = true) private String message;
    @Column(nullable = false, updatable = false) @Builder.Default private LocalDateTime donationTimestamp = LocalDateTime.now();
}