package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.Announcement;
import com.bookstore.online_bookstore.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/announcements")
public class AdminAnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    // CREATE - show form
    @GetMapping
    public String showForm(Model model) {
        model.addAttribute("announcement", new Announcement());
        return "announcement-form";
    }

    // CREATE - save
    @PostMapping
    public String saveAnnouncement(@ModelAttribute Announcement announcement,
                                   RedirectAttributes ra) {
        announcementService.saveAnnouncement(announcement);
        ra.addFlashAttribute("successMessage", "Announcement posted successfully!");
        return "redirect:/announcements";
    }

    // READ - list all (admin view)
    @GetMapping("/list")
    public String listAnnouncements(Model model) {
        model.addAttribute("announcements", announcementService.getAllAnnouncements());
        return "announcement-list";
    }

    // UPDATE - show edit form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("announcement", announcementService.getAnnouncementById(id));
        return "announcement-form";
    }

    // UPDATE - save edited
    @PostMapping("/edit/{id}")
    public String updateAnnouncement(@PathVariable Long id,
                                     @ModelAttribute Announcement announcement,
                                     RedirectAttributes ra) {
        announcementService.updateAnnouncement(id, announcement);
        ra.addFlashAttribute("successMessage", "Announcement updated successfully!");
        return "redirect:/admin/announcements/list";
    }

    // DELETE
    @PostMapping("/delete/{id}")
    public String deleteAnnouncement(@PathVariable Long id, RedirectAttributes ra) {
        announcementService.deleteAnnouncement(id);
        ra.addFlashAttribute("successMessage", "Announcement deleted.");
        return "redirect:/admin/announcements/list";
    }
}