package id.ac.ui.cs.advprog.gatherlove.campaign.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CampaignDto {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    @DecimalMin("10000.00")
    private BigDecimal targetAmount;

    @NotNull
    private LocalDate deadline;

    private String imageUrl;
}
