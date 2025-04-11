package id.ac.ui.cs.advprog.gatherlove.admin.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.UUID;

public class AnnouncementTest {

    @Test
    void testAnnouncementGettersAndSetters() {
        Announcement announcement = new Announcement();
        UUID id = UUID.randomUUID();
        String title = "Test Title";
        String content = "Test Content";
        Date date = new Date();

        announcement.setId(id);
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setDate(date);

        assertEquals(id, announcement.getId());
        assertEquals(title, announcement.getTitle());
        assertEquals(content, announcement.getContent());
        assertEquals(date, announcement.getDate());
    }

    @Test
    void testAnnouncementDefaultValues() {
        Announcement announcement = new Announcement();

        assertNotNull(announcement.getId());
        assertNull(announcement.getTitle());
        assertNull(announcement.getContent());
        assertNotNull(announcement.getDate());
    }
}