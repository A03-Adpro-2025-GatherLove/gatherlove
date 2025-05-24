package id.ac.ui.cs.advprog.gatherlove.profile.model;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    @Id
    private UUID id;  // Same type as UserEntity.id

    private String fullName;

    private String phoneNumber;

    private String bio;

    @OneToOne
    @MapsId  // This will use the user's ID as the profile ID
    @JoinColumn(name = "id")  // The column name in profile table
    private UserEntity user;
}