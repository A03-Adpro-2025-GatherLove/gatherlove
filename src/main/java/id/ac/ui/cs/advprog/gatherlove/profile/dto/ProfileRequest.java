package id.ac.ui.cs.advprog.gatherlove.profile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {
    private String fullName;

    private String phoneNumber;

    private String bio;
    
}
