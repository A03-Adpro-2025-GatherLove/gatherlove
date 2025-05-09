package id.ac.ui.cs.advprog.gatherlove.profile.repository;

import id.ac.ui.cs.advprog.gatherlove.profile.model.Profile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProfileRepositoryTest {

    @Autowired
    private ProfileRepository repository;

    @Test
    @DisplayName("Should save and retrieve a profile by ID")
    void saveAndFindById() {
        Profile profile = new Profile();
        profile.setName("Alice");
        profile.setEmail("alice@example.com");
        profile.setPhoneNumber("08999999999");
        profile.setBio("Hi, I'm Alice");

        Profile saved = repository.save(profile);

        Optional<Profile> result = repository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("Should return empty if profile ID does not exist")
    void findById_nonExistent_shouldReturnEmpty() {
        Optional<Profile> result = repository.findById(999L);
        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("Should update an existing profile")
    void updateProfile() {
        Profile profile = new Profile();
        profile.setName("Bob");
        profile.setEmail("bob@example.com");
        profile.setPhoneNumber("081212121212");
        profile.setBio("Hello world");
        Profile saved = repository.save(profile);

        saved.setBio("Updated bio");
        Profile updated = repository.save(saved);

        assertThat(updated.getBio()).isEqualTo("Updated bio");
    }

    @Test
    @DisplayName("Should delete a profile")
    void deleteProfile() {
        Profile profile = new Profile();
        profile.setName("Charlie");
        profile.setEmail("charlie@example.com");
        profile.setPhoneNumber("0800000000");
        profile.setBio("Bye");

        Profile saved = repository.save(profile);
        Long id = saved.getId();

        repository.delete(saved);

        Optional<Profile> result = repository.findById(id);
        assertThat(result).isEmpty();
    }
}
