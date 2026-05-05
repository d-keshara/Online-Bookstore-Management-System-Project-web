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
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AuthorWebController {

    private final AuthorService authorService;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public AuthorWebController(AuthorService authorService,
                               UserRepository userRepository,
                               BookRepository bookRepository) {
        this.authorService = authorService;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }


    @GetMapping("/admin/authors")
    public String listAuthors(Model model, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Author> authorPage = authorService.getAllAuthors(pageable);

        model.addAttribute("authors", authorPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", authorPage.getTotalPages());

        return "authors-list";
    }


    // Loads the Author Dashboard and their specific book submissions
    @GetMapping("/author/dashboard")
    public String showAuthorDashboard(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        User currentUser = userRepository.findByUsername(principal.getName()).orElse(null);

        if (currentUser != null && currentUser.getAuthor() != null) {
            // Safely filters all books to find only the ones belonging to this specific author
            List<Book> myBooks = bookRepository.findAll().stream()
                    .filter(book -> book.getAuthor() != null &&
                            book.getAuthor().getId().equals(currentUser.getAuthor().getId()))
                    .collect(Collectors.toList());

            model.addAttribute("books", myBooks);
            model.addAttribute("username", currentUser.getUsername());
        }

        return "author-dashboard";
    }

    @GetMapping("/author/books/request")
    public String showRequestBookForm(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        model.addAttribute("book", new Book());
        return "author-request-book";
    }

    @PostMapping("/author/books/request/save")
    public String submitBookRequest(@ModelAttribute("book") Book book,
                                    Principal principal,
                                    RedirectAttributes redirectAttributes) {

        if (principal == null) return "redirect:/login";

        User currentUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Author authorProfile = currentUser.getAuthor();
        if (authorProfile == null) {
            return "redirect:/login";
        }

        book.setAuthor(authorProfile);
        book.setApprovalStatus("PENDING");

        bookRepository.save(book);

        redirectAttributes.addFlashAttribute("successMessage",
                "Success! Your book '" + book.getTitle() + "' has been submitted for Admin approval.");

        return "redirect:/author/dashboard";
    }
}