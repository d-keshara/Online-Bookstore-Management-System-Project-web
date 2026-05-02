package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.Author;
import com.bookstore.online_bookstore.entity.User;
import com.bookstore.online_bookstore.repository.AuthorRepository;
import com.bookstore.online_bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/author")
public class AuthorPortalController {

    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorPortalController(UserRepository userRepository, AuthorRepository authorRepository) {
        this.userRepository = userRepository;
        this.authorRepository = authorRepository;
    }

    @GetMapping("/dashboard")
    public String showAuthorDashboard(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "author-dashboard";
    }

    @GetMapping("/my-books")
    public String showMyBooks(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Author author = authorRepository.findByUser(user);

        if (author == null) {
            return "redirect:/author/dashboard?error=no_profile";
        }

        model.addAttribute("author", author);
        model.addAttribute("username", principal.getName());
        return "author-books";
    }

    @GetMapping("/edit-profile")
    public String showEditProfileForm(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Author author = authorRepository.findByUser(user);

        if (author == null) {
            // Debugging line: check your console if the redirect happens again
            System.out.println("No author profile found for user: " + principal.getName());
            return "redirect:/author/dashboard?error=no_profile";
        }

        model.addAttribute("author", author);
        model.addAttribute("username", principal.getName());
        return "author-edit-profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute("author") Author authorDetails, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Author existingAuthor = authorRepository.findByUser(user);

        if (existingAuthor != null) {
            existingAuthor.setName(authorDetails.getName());
            existingAuthor.setBiography(authorDetails.getBiography());
            existingAuthor.setProfileImageUrl(authorDetails.getProfileImageUrl());
            authorRepository.save(existingAuthor);
        }

        return "redirect:/author/dashboard?success=profile_updated";
    }
}