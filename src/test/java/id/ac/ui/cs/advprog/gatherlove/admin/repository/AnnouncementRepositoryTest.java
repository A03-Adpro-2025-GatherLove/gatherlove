// src/test/java/id/ac/ui/cs/advprog/gatherlove/admin/repository/AnnouncementRepositoryTest.java
package id.ac.ui.cs.advprog.gatherlove.admin.repository;

import id.ac.ui.cs.advprog.gatherlove.admin.model.Announcement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AnnouncementRepositoryTest {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Test
    void testFindById() {
        Announcement announcement = new Announcement();
        announcement.setTitle("Test Title");
        announcement.setContent("Test Content");
        announcement = announcementRepository.save(announcement);

        Announcement foundAnnouncement = announcementRepository.findById(announcement.getId()).orElse(null);

        assertNotNull(foundAnnouncement);
        assertEquals(announcement.getTitle(), foundAnnouncement.getTitle());
        assertEquals(announcement.getContent(), foundAnnouncement.getContent());
    }

    @Test
    void testFindByIdNotFound() {
        Announcement foundAnnouncement = announcementRepository.findById(UUID.randomUUID()).orElse(null);

        assertNull(foundAnnouncement);
    }

    @Test
    void testSave() {
        Announcement announcement = new Announcement();
        announcement.setTitle("Test Title");
        announcement.setContent("Test Content");

        Announcement savedAnnouncement = announcementRepository.save(announcement);

        assertNotNull(savedAnnouncement);
        assertNotNull(savedAnnouncement.getId());
        assertEquals(announcement.getTitle(), savedAnnouncement.getTitle());
        assertEquals(announcement.getContent(), savedAnnouncement.getContent());
    }

    @Test
    void testDelete() {
        Announcement announcement = new Announcement();
        announcement.setTitle("Test Title");
        announcement.setContent("Test Content");
        announcement = announcementRepository.save(announcement);

        announcementRepository.delete(announcement);

        Announcement foundAnnouncement = announcementRepository.findById(announcement.getId()).orElse(null);

        assertNull(foundAnnouncement);
    }
}