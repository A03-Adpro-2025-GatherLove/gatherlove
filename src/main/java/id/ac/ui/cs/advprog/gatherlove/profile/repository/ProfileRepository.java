package id.ac.ui.cs.advprog.gatherlove.profile.repository;

import id.ac.ui.cs.advprog.gatherlove.profile.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}