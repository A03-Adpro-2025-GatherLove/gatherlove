package id.ac.ui.cs.advprog.gatherlove.profile.model;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProfileUserEntityIntegrationTest {

    @Test
    void testProfileUserEntityRelationship() {
        // Create a user
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUsername("johndoe");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        
        // Create profile using builder with reference to user
        Profile profile = Profile.builder()
                .id(userId)  // Same ID as user
                .fullName("John Doe")
                .phoneNumber("+62123456789")
                .bio("Test bio")
                .user(user)
                .build();
        
        // Verify relationship
        assertNotNull(profile.getUser());
        assertEquals(user, profile.getUser());
        assertEquals(userId, profile.getId());
        assertEquals(profile.getId(), profile.getUser().getId());
        assertEquals("johndoe", profile.getUser().getUsername());
        assertEquals("john@example.com", profile.getUser().getEmail());
    }
    
    @Test
    void testProfileWithNullUser() {
        // Profile can exist with null user reference
        Profile profile = Profile.builder()
                .id(UUID.randomUUID())
                .fullName("John Doe")
                .phoneNumber("+62123456789")
                .bio("Test bio")
                .user(null)
                .build();
        
        assertNull(profile.getUser());
        assertNotNull(profile.getId());
    }
}
