package id.ac.ui.cs.advprog.gatherlove.profile.repository;

import id.ac.ui.cs.advprog.gatherlove.profile.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
}