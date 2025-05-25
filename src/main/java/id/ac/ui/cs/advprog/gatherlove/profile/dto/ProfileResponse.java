package id.ac.ui.cs.advprog.gatherlove.profile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private UUID id;
    private String fullName;
    private String phoneNumber;
    private String bio;
}