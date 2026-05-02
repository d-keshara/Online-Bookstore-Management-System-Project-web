package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.Book;
import com.bookstore.online_bookstore.entity.Review;
import com.bookstore.online_bookstore.entity.User;
import com.bookstore.online_bookstore.repository.BookRepository;
import com.bookstore.online_bookstore.repository.ReviewRepository;
import com.bookstore.online_bookstore.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ReviewController(ReviewRepository reviewRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/reviews/add")
    public String addReview(@RequestParam Integer bookId,
                            @RequestParam int rating,
                            @RequestParam String comment,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // PREVENT SPAM: Check if this user already reviewed this book
        if (reviewRepository.existsByUserAndBook(user, book)) {
            redirectAttributes.addFlashAttribute("error", "You have already reviewed this book!");
            return "redirect:/catalog/details/" + bookId;
        }

        Review review = new Review();
        review.setBook(book);
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());

        reviewRepository.save(review);

        redirectAttributes.addFlashAttribute("success", "Review submitted successfully!");
        return "redirect:/catalog/details/" + bookId;
    }
}