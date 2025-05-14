package id.ac.ui.cs.advprog.gatherlove.admin.component;

import id.ac.ui.cs.advprog.gatherlove.admin.model.Announcement;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserDashboardTest {

    @Test
    public void testUpdateAnnouncement() {
        // Create a new UserDashboard instance
        UserDashboard userDashboard = new UserDashboard();

        // Create a new Announcement instance
        Announcement announcement = new Announcement();

        // Update the announcement in the UserDashboard
        userDashboard.update(announcement);

        // Verify that the announcement is updated correctly
        assertEquals(announcement, userDashboard.announcement);
    }

    @Test
    public void testUpdateAnnouncementNull() {
        // Create a new UserDashboard instance
        UserDashboard userDashboard = new UserDashboard();

        // Update the announcement in the UserDashboard with null
        userDashboard.update(null);

        // Verify that the announcement is updated correctly
        assertNull(userDashboard.announcement);
    }
}