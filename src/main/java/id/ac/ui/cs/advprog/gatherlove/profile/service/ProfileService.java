package id.ac.ui.cs.advprog.gatherlove.profile.service;

import id.ac.ui.cs.advprog.gatherlove.profile.model.Profile;
import id.ac.ui.cs.advprog.gatherlove.profile.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {

    private final ProfileRepository repository;

    public ProfileService(ProfileRepository repository) {
        this.repository = repository;
    }

    public Profile completeProfile(Profile profile) {
        return repository.save(profile);
    }

    public Optional<Profile> viewProfile(Long id) {
        return repository.findById(id);
    }

    public Profile updateProfile(Long id, Profile updated) {
        Profile existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setPhoneNumber(updated.getPhoneNumber());
        existing.setBio(updated.getBio());
        return repository.save(existing);
    }

    public Profile deleteBio(Long id) {
        Profile profile = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        profile.setBio(null);
        return repository.save(profile);
    }
}