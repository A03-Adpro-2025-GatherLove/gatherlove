package id.ac.ui.cs.advprog.gatherlove.admin.repository;

import id.ac.ui.cs.advprog.gatherlove.admin.model.Announcement;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, UUID> {
    
}
