package id.ac.ui.cs.advprog.gatherlove.admin.service;

import id.ac.ui.cs.advprog.gatherlove.admin.component.AnnouncementObserver;
import id.ac.ui.cs.advprog.gatherlove.admin.model.Announcement;
import id.ac.ui.cs.advprog.gatherlove.admin.repository.AnnouncementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnnouncementServiceImplTest {

    @Mock
    private AnnouncementRepository announcementRepository;

    @Mock
    private AnnouncementObserver announcementObserver;

    @InjectMocks
    private AnnouncementServiceImpl announcementService;

    @Test
    void testSendAnnouncement() {
        announcementService.addObserver(announcementObserver);
        
        Announcement announcement = new Announcement();
        announcement.setTitle("Test Title");
        announcement.setContent("Test Content");

        announcementService.sendAnnouncement(announcement);

        verify(announcementRepository).save(announcement);
        verify(announcementObserver).update(announcement);
    }

    @Test
    void testAddObserver() {
        announcementService.addObserver(announcementObserver);

        assertEquals(1, announcementService.getObservers().size());
    }

    @Test
    void testRemoveObserver() {
        announcementService.addObserver(announcementObserver);
        announcementService.removeObserver(announcementObserver);

        assertEquals(0, announcementService.getObservers().size());
    }

    @Test
    void testNotifyObservers() {
        announcementService.addObserver(announcementObserver);

        Announcement announcement = new Announcement();
        announcement.setTitle("Test Title");
        announcement.setContent("Test Content");

        announcementService.notifyObservers(announcement);

        verify(announcementObserver).update(announcement);
    }
}