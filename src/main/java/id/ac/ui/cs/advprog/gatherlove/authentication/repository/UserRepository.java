package id.ac.ui.cs.advprog.gatherlove.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsername(String username);
    
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findById(UUID id);
    
    Boolean existsByUsername(String username);
    
    Boolean existsByEmail(String email);
}