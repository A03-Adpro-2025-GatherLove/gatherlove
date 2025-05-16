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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @NotBlank
    private String name;

    @NotBlank
    private String phoneNumber;

    private String bio;

    @OneToOne
    @MapsId  // This annotation indicates that the ID is shared with the parent
    @JoinColumn(name = "id")  // The column name is the same as the ID
    private UserEntity user;
}
