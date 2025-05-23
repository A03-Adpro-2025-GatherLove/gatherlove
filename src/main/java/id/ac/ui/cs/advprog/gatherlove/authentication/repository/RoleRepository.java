package id.ac.ui.cs.advprog.gatherlove.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.ERole;
import id.ac.ui.cs.advprog.gatherlove.authentication.model.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}