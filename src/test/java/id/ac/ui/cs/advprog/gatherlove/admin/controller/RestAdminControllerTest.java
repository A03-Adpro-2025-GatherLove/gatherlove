package id.ac.ui.cs.advprog.gatherlove.admin.controller;

import id.ac.ui.cs.advprog.gatherlove.admin.model.Announcement;
import id.ac.ui.cs.advprog.gatherlove.admin.service.AnnouncementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestAdminControllerTest {

    @Mock
    private AnnouncementService announcementService;

    @InjectMocks
    private RestAdminController restAdminController;

    @Test
    void testAdmin() {
        String result = restAdminController.admin();
        assertEquals("Welcome to the admin dashboard", result);
    }

    @Test
    void testSendAnnouncement() {
        Announcement announcement = new Announcement();
        announcement.setTitle("Test Title");
        announcement.setContent("Test Content");

        ResponseEntity<String> result = restAdminController.sendAnnouncement(announcement);
        assertEquals("Announcement sent successfully", result.getBody());
        assertEquals(200, result.getStatusCode().value());

        verify(announcementService).sendAnnouncement(announcement);
    }

    @Test
    void testSendAnnouncementInvalid() {
        Announcement announcement = new Announcement();

        ResponseEntity<String> result = restAdminController.sendAnnouncement(announcement);
        assertEquals("Title and content are required", result.getBody());
        assertEquals(400, result.getStatusCode().value());
    }
}