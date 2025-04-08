package id.ac.ui.cs.advprog.gatherlove.admin.component;

import id.ac.ui.cs.advprog.gatherlove.admin.model.Announcement;

public class UserDashboard implements AnnouncementObserver {
    Announcement announcement;

    @Override
    public void update(Announcement announcement) {
        this.announcement = announcement;
    }
}
