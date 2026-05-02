package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.Book;
import com.bookstore.online_bookstore.entity.Order;
import com.bookstore.online_bookstore.entity.Review;
import com.bookstore.online_bookstore.entity.User;
import com.bookstore.online_bookstore.repository.BookRepository;
import com.bookstore.online_bookstore.repository.ReviewRepository;
import com.bookstore.online_bookstore.repository.UserRepository;
import com.bookstore.online_bookstore.service.OrderService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

import java.util.List;

@Controller
public class UserWebController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    public UserWebController(OrderService orderService,
                             UserRepository userRepository,
                             PasswordEncoder passwordEncoder,
                             BookRepository bookRepository,
                             ReviewRepository reviewRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.bookRepository = bookRepository;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/catalog/details/{id}")
    public String showBookDetails(@PathVariable Integer id, Model model) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));

        model.addAttribute("book", book);
        return "details";
    }

    @GetMapping("/my-orders")
    public String showMyOrders(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        List<Order> myOrders = orderService.getOrdersByUsername(username);

        model.addAttribute("orders", myOrders);
        model.addAttribute("username", username);

        return "order-history";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/my-reviews")
    public String showMyReviews(Model model, Principal principal) {
        // FIX: Added null check to prevent 500 error if not logged in
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Review> myReviews = reviewRepository.findByUser(user);

        model.addAttribute("reviews", myReviews);
        model.addAttribute("user", user);

        return "my-reviews";
    }

    @GetMapping("/my-account")
    public String showMyAccount(Model model, Principal principal) {
        // Principal holds the username (email) of the currently logged-in user
        if (principal == null) {
            return "redirect:/login";
        }

        // Find the user in the database
        User currentUser = userRepository.findByUsername(principal.getName()).orElse(null);

        if (currentUser == null) {
            return "redirect:/login";
        }

        // Send the user data to the HTML page
        model.addAttribute("user", currentUser);
        return "my-account";
    }

    @PostMapping("/register/save")
    public String saveNewUser(@ModelAttribute("user") User user) {
        user.setRole("USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/login?registered=true";
    }
    @PostMapping("/reviews/delete/{id}")
    public String deleteReview(@PathVariable Integer id) {
        // Fires the direct SQL delete, bypassing Hibernate's lazy checking
        reviewRepository.forceDeleteById(id);

        return "redirect:/my-reviews";
    }

    // NEW: Handles the Edit button click and loads the edit form
    @GetMapping("/reviews/edit/{id}")
    public String showEditReviewForm(@PathVariable Integer id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid review Id:" + id));

        // Ensure users can only edit their own reviews
        if (!review.getUser().getUsername().equals(principal.getName())) {
            return "redirect:/my-reviews";
        }

        model.addAttribute("review", review);
        return "edit-review"; // This looks for an edit-review.html file
    }

    @PostMapping("/reviews/update/{id}")
    public String updateReview(@PathVariable Integer id, @ModelAttribute("review") Review updatedReview) {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid review Id:" + id));

        // Update the fields
        existingReview.setRating(updatedReview.getRating());
        existingReview.setComment(updatedReview.getComment());

        reviewRepository.save(existingReview);
        return "redirect:/my-reviews";
    }
    @PostMapping("/account/update")
    public String updateProfile(@RequestParam String fullName,
                                @RequestParam String phoneNumber,
                                @RequestParam String address,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {

        if (principal == null) return "redirect:/login";

        User currentUser = userRepository.findByUsername(principal.getName()).orElse(null);

        if (currentUser != null) {
            // Update the allowed fields
            currentUser.setFullName(fullName);
            currentUser.setPhoneNumber(phoneNumber);
            currentUser.setAddress(address);

            // Save back to the database
            userRepository.save(currentUser);

            // Send a success message to the front-end
            redirectAttributes.addFlashAttribute("successMessage", "Profile details updated successfully!");
        }

        return "redirect:/my-account";
    }

    @PostMapping("/account/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {

        if (principal == null) return "redirect:/login";

        User currentUser = userRepository.findByUsername(principal.getName()).orElse(null);

        if (currentUser != null) {
            // Check 1: Does the current password match what's in the database?
            if (!passwordEncoder.matches(currentPassword, currentUser.getPassword())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Your current password was incorrect.");
                return "redirect:/my-account";
            }

            // Check 2: Did they type the new password correctly twice?
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "New passwords do not match!");
                return "redirect:/my-account";
            }

            // If both checks pass, encrypt the new password and save it
            currentUser.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(currentUser);

            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
        }

        return "redirect:/my-account";
    }
}