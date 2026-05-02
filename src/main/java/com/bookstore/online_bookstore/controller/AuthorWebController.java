package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.Author;
import com.bookstore.online_bookstore.entity.Book;
import com.bookstore.online_bookstore.entity.User;
import com.bookstore.online_bookstore.repository.BookRepository;
import com.bookstore.online_bookstore.repository.UserRepository;
import com.bookstore.online_bookstore.service.AuthorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class AuthorWebController {

    private final AuthorService authorService;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    // We inject all three repositories/services here
    public AuthorWebController(AuthorService authorService,
                               UserRepository userRepository,
                               BookRepository bookRepository) {
        this.authorService = authorService;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    // ==========================================
    // ADMIN METHODS
    // ==========================================

    // 1. View all authors (Paginated)
    @GetMapping("/admin/authors")
    public String listAuthors(Model model, @RequestParam(defaultValue = "0") int page) {
        // Fetch 10 authors per page using the existing service
        Pageable pageable = PageRequest.of(page, 10);
        Page<Author> authorPage = authorService.getAllAuthors(pageable);

        model.addAttribute("authors", authorPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", authorPage.getTotalPages());

        return "authors-list";
    }

    // ==========================================
    // AUTHOR PORTAL METHODS
    // ==========================================

    // 1. Shows the Book Request Form
    @GetMapping("/author/books/request")
    public String showRequestBookForm(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        // Send a blank book object to the form
        model.addAttribute("book", new Book());
        return "author-request-book";
    }

    // 2. Handles the Form Submission
    @PostMapping("/author/books/request/save")
    public String submitBookRequest(@ModelAttribute("book") Book book,
                                    Principal principal,
                                    RedirectAttributes redirectAttributes) {

        if (principal == null) return "redirect:/login";

        // Find the currently logged-in user
        User currentUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get their glued author profile
        Author authorProfile = currentUser.getAuthor();
        if (authorProfile == null) {
            return "redirect:/login"; // Security check
        }

        // Set the hidden fields automatically
        book.setAuthor(authorProfile);
        book.setApprovalStatus("PENDING");

        // Note: Price and Stock are left empty for the Admin to fill out later!

        // Save the pending book to the database
        bookRepository.save(book);

        redirectAttributes.addFlashAttribute("successMessage",
                "Success! Your book '" + book.getTitle() + "' has been submitted for Admin approval.");

        return "redirect:/author/dashboard";
    }
}