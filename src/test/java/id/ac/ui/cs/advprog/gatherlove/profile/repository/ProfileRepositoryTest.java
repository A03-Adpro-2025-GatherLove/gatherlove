package id.ac.ui.cs.advprog.gatherlove.profile.repository;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.Role;
import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import id.ac.ui.cs.advprog.gatherlove.profile.model.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProfileRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProfileRepository profileRepository;

    private UserEntity testUser;
    private Profile testProfile;

    @BeforeEach
    void setUp() {
        // Create and persist a test user
        testUser = new UserEntity();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        entityManager.persist(testUser);
        entityManager.flush();

        // Create a test profile linked to the user
        testProfile = Profile.builder()
                .fullName("Test User")
                .phoneNumber("+1234567890")
                .bio("This is a test bio")
                .user(testUser)
                .build();
        
        // Note: we don't set the ID as it should use the user's ID through @MapsId
        entityManager.persist(testProfile);
        entityManager.flush();
    }

    @Test
    void whenSaveProfile_thenProfileIsPersisted() {
        // Given a new profile
        UserEntity newUser = new UserEntity();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setPassword("password456");
        entityManager.persist(newUser);
        
        Profile newProfile = Profile.builder()
                .fullName("New User")
                .phoneNumber("+9876543210")
                .bio("New user bio")
                .user(newUser)
                .build();
        
        // When saving the profile
        Profile savedProfile = profileRepository.save(newProfile);
        
        // Then it should be persisted with the same ID as the user
        assertThat(savedProfile).isNotNull();
        assertThat(savedProfile.getId()).isEqualTo(newUser.getId());
        assertThat(savedProfile.getFullName()).isEqualTo("New User");
    }

    @Test
    void whenFindById_thenReturnProfile() {
        // When finding by ID
        Optional<Profile> found = profileRepository.findById(testUser.getId());
        
        // Then the profile should be found
        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("Test User");
        assertThat(found.get().getUser().getUsername()).isEqualTo("testuser");
    }

    @Test
    void whenFindByNonExistentId_thenReturnEmpty() {
        // When finding by a non-existent ID
        Optional<Profile> found = profileRepository.findById(UUID.randomUUID());
        
        // Then no profile should be found
        assertThat(found).isEmpty();
    }

    @Test
    void whenFindAll_thenReturnAllProfiles() {
        // Given another profile
        UserEntity anotherUser = new UserEntity();
        anotherUser.setUsername("anotheruser");
        anotherUser.setEmail("another@example.com");
        anotherUser.setPassword("password789");
        entityManager.persist(anotherUser);
        
        Profile anotherProfile = Profile.builder()
                .fullName("Another User")
                .phoneNumber("+1122334455")
                .bio("Another user's bio")
                .user(anotherUser)
                .build();
        entityManager.persist(anotherProfile);
        entityManager.flush();
        
        // When finding all profiles
        List<Profile> profiles = profileRepository.findAll();
        
        // Then all profiles should be returned
        assertThat(profiles).hasSize(2);
    }

    @Test
    void whenUpdateProfile_thenProfileIsUpdated() {
        // Given a profile with updated fields
        testProfile.setFullName("Updated Name");
        testProfile.setBio("Updated bio information");
        
        // When updating the profile
        Profile updatedProfile = profileRepository.save(testProfile);
        
        // Then the profile should be updated
        assertThat(updatedProfile.getFullName()).isEqualTo("Updated Name");
        assertThat(updatedProfile.getBio()).isEqualTo("Updated bio information");
        
        // Verify in database
        Profile foundProfile = entityManager.find(Profile.class, testUser.getId());
        assertThat(foundProfile.getFullName()).isEqualTo("Updated Name");
    }

    @Test
    void whenDeleteProfile_thenProfileIsRemoved() {
        // Given a profile ID
        UUID profileId = testProfile.getId();
        
        // When deleting the profile
        profileRepository.deleteById(profileId);
        entityManager.flush();
        
        // Then the profile should be removed
        Profile foundProfile = entityManager.find(Profile.class, profileId);
        assertThat(foundProfile).isNull();
    }

    @Test
    void whenProfileSaved_thenUserRelationshipIsPreserved() {
        // Given a profile
        Profile foundProfile = profileRepository.findById(testUser.getId()).orElseThrow();
        
        // Then the user relationship should be preserved
        assertThat(foundProfile.getUser()).isNotNull();
        assertThat(foundProfile.getUser().getUsername()).isEqualTo("testuser");
        assertThat(foundProfile.getUser().getEmail()).isEqualTo("test@example.com");
    }
}
