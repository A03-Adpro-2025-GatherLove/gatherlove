package id.ac.ui.cs.advprog.gatherlove.admin.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.gatherlove.admin.component.AnnouncementObserver;
import id.ac.ui.cs.advprog.gatherlove.admin.model.Announcement;
import id.ac.ui.cs.advprog.gatherlove.admin.repository.AnnouncementRepository;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {
    
    private List<AnnouncementObserver> observers = new ArrayList<>();
    
    @Autowired
    private AnnouncementRepository announcementRepository;
    
    @Override
    public void addObserver(AnnouncementObserver observer) {
        observers.add(observer);
    }
    
    @Override
    public void removeObserver(AnnouncementObserver observer) {
        observers.remove(observer);
    }
    
    @Override
    public void notifyObservers(Announcement announcement) {
        for (AnnouncementObserver observer : observers) {
            observer.update(announcement);
        }
    }
    
    @Override
    public void sendAnnouncement(Announcement announcement) {
        announcementRepository.save(announcement);
        notifyObservers(announcement);
    }
}