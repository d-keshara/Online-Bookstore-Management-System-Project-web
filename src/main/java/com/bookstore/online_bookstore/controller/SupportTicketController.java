package com.bookstore.online_bookstore.controller;



import com.bookstore.online_bookstore.entity.SupportTicket;
import com.bookstore.online_bookstore.entity.User;
import com.bookstore.online_bookstore.repository.SupportTicketRepository;
import com.bookstore.online_bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

    @Controller
    public class SupportTicketController {

        @Autowired
        private SupportTicketRepository ticketRepository;

        @Autowired
        private UserRepository userRepository;



        @GetMapping("/contact")
        public String showContactForm(Model model) {
            model.addAttribute("ticket", new SupportTicket());
            return "contact-us";
        }

        @PostMapping("/contact/submit")
        public String submitTicket(@ModelAttribute SupportTicket ticket,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   RedirectAttributes ra) {
            if (userDetails == null) {
                return "redirect:/login"; // Must be logged in to send a ticket
            }

            // Find the currently logged-in user and attach them to the ticket
            User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
            ticket.setUser(user);

            ticketRepository.save(ticket);
            ra.addFlashAttribute("successMessage", "Your ticket has been submitted! Our team will review it shortly.");
            return "redirect:/contact";
        }



        @GetMapping("/admin/tickets")
        public String viewAllTickets(Model model) {
            // Fetch all tickets from the database to show the Admin
            List<SupportTicket> allTickets = ticketRepository.findAll();
            model.addAttribute("tickets", allTickets);
            return "admin-tickets";
        }

        @PostMapping("/admin/tickets/resolve/{id}")
        public String resolveTicket(@PathVariable("id") Integer id, RedirectAttributes ra) {
            SupportTicket ticket = ticketRepository.findById(id).orElse(null);
            if (ticket != null) {
                ticket.setStatus("RESOLVED");
                ticketRepository.save(ticket);
                ra.addFlashAttribute("successMessage", "Ticket #" + id + " marked as RESOLVED.");
            }
            return "redirect:/admin/tickets";
        }
    }

