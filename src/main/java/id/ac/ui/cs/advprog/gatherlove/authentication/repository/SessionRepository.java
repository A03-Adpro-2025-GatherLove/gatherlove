package id.ac.ui.cs.advprog.gatherlove.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.Session;
import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByToken(String token);
    
    List<Session> findByUserAndIsValid(UserEntity user, boolean isValid);
    
    void deleteByUser(UserEntity user);
}