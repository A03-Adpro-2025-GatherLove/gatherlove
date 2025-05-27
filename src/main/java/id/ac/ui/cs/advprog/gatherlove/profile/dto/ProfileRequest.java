package id.ac.ui.cs.advprog.gatherlove.profile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {
    private String fullName;

    private String phoneNumber;

    private String bio;
    
    // Static method to create a new Builder
    public static Builder builder() {
        return new Builder();
    }
    
    // Builder static inner class
    public static class Builder {
        private String fullName;
        private String phoneNumber;
        private String bio;
        
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
        
        public ProfileRequest build() {
            return new ProfileRequest(fullName, phoneNumber, bio);
        }
    }
}
