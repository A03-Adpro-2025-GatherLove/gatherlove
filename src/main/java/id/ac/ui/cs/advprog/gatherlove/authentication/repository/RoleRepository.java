package id.ac.ui.cs.advprog.gatherlove.authentication.repository;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}