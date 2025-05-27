package id.ac.ui.cs.advprog.gatherlove.profile.model;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
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
    
    // Static method to create a new Builder
    public static Builder builder() {
        return new Builder();
    }
    
    // Builder class
    public static class Builder {
        private UUID id;
        private String fullName;
        private String phoneNumber;
        private String bio;
        private UserEntity user;
        
        public Builder id(UUID id) {
            this.id = id;
            return this;
        }
        
        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }
        
        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }
        
        public Builder bio(String bio) {
            this.bio = bio;
            return this;
        }
        
        public Builder user(UserEntity user) {
            this.user = user;
            return this;
        }
        
        public Profile build() {
            return new Profile(id, fullName, phoneNumber, bio, user);
        }
    }
}
