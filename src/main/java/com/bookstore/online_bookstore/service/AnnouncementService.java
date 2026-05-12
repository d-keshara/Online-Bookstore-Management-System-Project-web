package com.bookstore.online_bookstore.service;

import com.bookstore.online_bookstore.entity.Announcement;
import com.bookstore.online_bookstore.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }

    public void saveAnnouncement(Announcement announcement) {
        announcementRepository.save(announcement);
    }

    // Read by ID
    public Announcement getAnnouncementById(Long id) {
        return announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));
    }

    // Update
    public void updateAnnouncement(Long id, Announcement updated) {
        Announcement existing = getAnnouncementById(id);
        existing.setTitle(updated.getTitle());
        existing.setContent(updated.getContent());
        announcementRepository.save(existing);
    }

    // Delete
    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }
}