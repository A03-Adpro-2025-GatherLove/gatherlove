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
    @DecimalMin(value = "10000.00", message = "Target dana minimal Rp10.000")
    private BigDecimal targetAmount;

    @NotNull(message = "Deadline tidak boleh kosong")
    @Future(message = "Deadline harus di masa depan")
    private LocalDate deadline;

    private String imageUrl;
}
