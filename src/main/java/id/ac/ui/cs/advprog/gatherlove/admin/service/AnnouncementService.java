package id.ac.ui.cs.advprog.gatherlove.admin.service;

import java.util.List;

import id.ac.ui.cs.advprog.gatherlove.admin.component.AnnouncementObserver;
import id.ac.ui.cs.advprog.gatherlove.admin.model.Announcement;

public interface AnnouncementService {
    void addObserver(AnnouncementObserver observer);

    void removeObserver(AnnouncementObserver observer);

    void notifyObservers(Announcement announcement);

    void sendAnnouncement(Announcement announcement);

    List<AnnouncementObserver> getObservers();
}
