package id.ac.ui.cs.advprog.gatherlove.profile.service;

import com.yourapp.profile.model.Profile;
import com.yourapp.profile.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ProfileServiceTest {

    @Mock
    private ProfileRepository repository;

    @InjectMocks
    private ProfileService service;

    private Profile dummy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dummy = new Profile();
        dummy.setId(1L);
        dummy.setName("John Doe");
        dummy.setEmail("john@example.com");
        dummy.setPhoneNumber("081234567890");
        dummy.setBio("Original bio");
    }

    @Test
    void completeProfile_shouldSaveProfile() {
        when(repository.save(any(Profile.class))).thenReturn(dummy);

        Profile saved = service.completeProfile(dummy);

        assertNotNull(saved);
        assertEquals("John Doe", saved.getName());
        verify(repository, times(1)).save(dummy);
    }

    @Test
    void viewProfile_shouldReturnProfileIfExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(dummy));

        Optional<Profile> result = service.viewProfile(1L);

        assertTrue(result.isPresent());
        assertEquals("john@example.com", result.get().getEmail());
    }

    @Test
    void updateProfile_shouldUpdateAndReturnProfile() {
        Profile updated = new Profile();
        updated.setName("Jane Doe");
        updated.setEmail("jane@example.com");
        updated.setPhoneNumber("081234567891");
        updated.setBio("Updated bio");

        when(repository.findById(1L)).thenReturn(Optional.of(dummy));
        when(repository.save(any(Profile.class))).thenReturn(updated);

        Profile result = service.updateProfile(1L, updated);

        assertEquals("Jane Doe", result.getName());
        assertEquals("Updated bio", result.getBio());
        verify(repository).save(dummy);
    }

    @Test
    void deleteBio_shouldClearBioAndReturnProfile() {
        when(repository.findById(1L)).thenReturn(Optional.of(dummy));

        dummy.setBio(null);
        when(repository.save(any(Profile.class))).thenReturn(dummy);

        Profile result = service.deleteBio(1L);

        assertNull(result.getBio());
        verify(repository).save(dummy);
    }

    @Test
    void viewProfile_shouldReturnEmptyIfNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<Profile> result = service.viewProfile(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void updateProfile_shouldThrowIfNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.updateProfile(99L, dummy);
        });

        assertEquals("Profile not found", ex.getMessage());
    }

    @Test
    void deleteBio_shouldThrowIfNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.deleteBio(99L);
        });

        assertEquals("Profile not found", ex.getMessage());
    }
}
