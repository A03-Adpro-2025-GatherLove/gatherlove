package id.ac.ui.cs.advprog.gatherlove.profile.model;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProfileTest {

    private Profile profile;
    private UserEntity user;
    private UUID testId;
    private String testFullName;
    private String testPhoneNumber;
    private String testBio;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testFullName = "John Doe";
        testPhoneNumber = "+62123456789";
        testBio = "This is my bio";

        // Create user entity
        user = new UserEntity();
        user.setId(testId);

        // Create profile manually for setter/getter tests
        profile = new Profile();
        profile.setId(testId);
        profile.setFullName(testFullName);
        profile.setPhoneNumber(testPhoneNumber);
        profile.setBio(testBio);
        profile.setUser(user);
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(testId, profile.getId());
        assertEquals(testFullName, profile.getFullName());
        assertEquals(testPhoneNumber, profile.getPhoneNumber());
        assertEquals(testBio, profile.getBio());
        assertEquals(user, profile.getUser());
    }

    @Test
    void testBuilderPattern() {
        Profile builtProfile = Profile.builder()
                .id(testId)
                .fullName(testFullName)
                .phoneNumber(testPhoneNumber)
                .bio(testBio)
                .user(user)
                .build();

        assertEquals(testId, builtProfile.getId());
        assertEquals(testFullName, builtProfile.getFullName());
        assertEquals(testPhoneNumber, builtProfile.getPhoneNumber());
        assertEquals(testBio, builtProfile.getBio());
        assertEquals(user, builtProfile.getUser());
    }

    @Test
    void testNoArgsConstructor() {
        Profile emptyProfile = new Profile();
        assertNull(emptyProfile.getId());
        assertNull(emptyProfile.getFullName());
        assertNull(emptyProfile.getPhoneNumber());
        assertNull(emptyProfile.getBio());
        assertNull(emptyProfile.getUser());
    }

    @Test
    void testAllArgsConstructor() {
        Profile fullProfile = new Profile(testId, testFullName, testPhoneNumber, testBio, user);
        
        assertEquals(testId, fullProfile.getId());
        assertEquals(testFullName, fullProfile.getFullName());
        assertEquals(testPhoneNumber, fullProfile.getPhoneNumber());
        assertEquals(testBio, fullProfile.getBio());
        assertEquals(user, fullProfile.getUser());
    }

    @Test
    void testIdMapping() {
        // When user has an ID, and we set the user on profile,
        // profile should use the same ID when we call setUser
        UUID userId = UUID.randomUUID();
        UserEntity newUser = new UserEntity();
        newUser.setId(userId);
        
        Profile newProfile = new Profile();
        newProfile.setUser(newUser);
        
        // Must manually set ID in tests since @MapsId works at persistence layer
        newProfile.setId(userId);
        
        assertEquals(userId, newProfile.getId());
        assertEquals(newUser, newProfile.getUser());
        assertEquals(newProfile.getId(), newProfile.getUser().getId());
    }
}
