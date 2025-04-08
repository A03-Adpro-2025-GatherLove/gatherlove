package id.ac.ui.cs.advprog.gatherlove.admin.service;

import id.ac.ui.cs.advprog.gatherlove.admin.component.AnnouncementObserver;
import id.ac.ui.cs.advprog.gatherlove.admin.model.Announcement;

public interface AnnouncementService {
    void addObserver(AnnouncementObserver observer);

    void removeObserver(AnnouncementObserver observer);

    void notifyObservers(Announcement announcement);

    void sendAnnouncement(String content);
}
