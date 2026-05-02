package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.Author;
import com.bookstore.online_bookstore.entity.Book;
import com.bookstore.online_bookstore.entity.InviteToken;
import com.bookstore.online_bookstore.entity.User;
import com.bookstore.online_bookstore.repository.AuthorRepository;
import com.bookstore.online_bookstore.repository.BookRepository;
import com.bookstore.online_bookstore.repository.InviteTokenRepository;
import com.bookstore.online_bookstore.repository.OrderRepository;
import com.bookstore.online_bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminWebController {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final UserRepository userRepository;
    private final InviteTokenRepository inviteTokenRepository;

    @Autowired
    public AdminWebController(BookRepository bookRepository,
                              OrderRepository orderRepository,
                              AuthorRepository authorRepository,
                              UserRepository userRepository,
                              InviteTokenRepository inviteTokenRepository) {
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
        this.authorRepository = authorRepository;
        this.userRepository = userRepository;
        this.inviteTokenRepository = inviteTokenRepository;
    }

    // --- DASHBOARD ---
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        // ONLY show "APPROVED" books in the main inventory table
        List<Book> approvedBooks = bookRepository.findAll().stream()
                .filter(book -> "APPROVED".equals(book.getApprovalStatus()))
                .collect(Collectors.toList());
        model.addAttribute("books", approvedBooks);

        List<InviteToken> availableTokens = inviteTokenRepository.findAll().stream()
                .filter(token -> !token.isUsed())
                .collect(Collectors.toList());
        model.addAttribute("unusedTokens", availableTokens);

        return "admin-dashboard";
    }

    // --- NEW: BOOK APPROVAL QUEUE ---
    @GetMapping("/requests")
    public String viewBookRequests(Model model) {
        // Fetch only books waiting for admin approval
        List<Book> pendingBooks = bookRepository.findAll().stream()
                .filter(book -> "PENDING".equals(book.getApprovalStatus()))
                .collect(Collectors.toList());

        model.addAttribute("pendingBooks", pendingBooks);
        return "admin-book-requests";
    }

    @GetMapping("/requests/review/{id}")
    public String reviewBookRequest(@PathVariable Integer id, Model model) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));
        model.addAttribute("book", book);
        return "admin-approve-book";
    }

    @PostMapping("/requests/approve/{id}")
    public String approveBookRequest(@PathVariable Integer id,
                                     @RequestParam BigDecimal price,
                                     @RequestParam Integer stock,
                                     RedirectAttributes redirectAttributes) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));

        // Admin sets the final details and approves it
        book.setPrice(price);
        book.setStock(stock);
        book.setApprovalStatus("APPROVED");

        bookRepository.save(book);
        redirectAttributes.addFlashAttribute("successMessage", "Book '" + book.getTitle() + "' published successfully!");

        return "redirect:/admin/dashboard";
    }

    // --- AUTHOR INVITE TOKEN GENERATOR ---
    @PostMapping("/tokens/generate")
    public String generateNewInviteToken(RedirectAttributes redirectAttributes) {
        String randomString = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String newCode = "NEXUS-" + randomString;

        InviteToken newToken = new InviteToken();
        newToken.setToken(newCode);
        inviteTokenRepository.save(newToken);

        redirectAttributes.addFlashAttribute("successMessage", "New author invite code generated: " + newCode);
        return "redirect:/admin/dashboard";
    }

    // --- AUTHOR MANAGEMENT ---
    @GetMapping("/authors/add")
    public String showAddAuthorForm(Model model) {
        model.addAttribute("author", new Author());
        return "add-author";
    }

    @PostMapping("/authors/save")
    public String saveAuthor(@ModelAttribute("author") Author author) {
        authorRepository.save(author);
        return "redirect:/admin/authors/add?success";
    }

    // --- BOOK MANAGEMENT ---
    @GetMapping("/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorRepository.findAll());
        return "add-book";
    }

    @PostMapping("/save")
    public String saveBook(@ModelAttribute("book") Book book) {
        // Books added directly by the Admin bypass the queue
        book.setApprovalStatus("APPROVED");
        bookRepository.save(book);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/edit/{id}")
    public String showEditBookForm(@PathVariable Integer id, Model model) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));
        model.addAttribute("book", book);
        model.addAttribute("authors", authorRepository.findAll());
        return "edit-book";
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Integer id) {
        bookRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }

    // --- ORDER MANAGEMENT ---
    @GetMapping("/orders")
    public String viewOrders(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "order-history";
    }

    // --- USER MANAGEMENT & PROMOTION ---
    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin-users";
    }

    @GetMapping("/users/promote/{id}")
    public String promoteToAdmin(@PathVariable Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        user.setRole("ROLE_ADMIN");
        userRepository.save(user);
        return "redirect:/admin/users?promoted";
    }

    @GetMapping("/users/promote-author/{id}")
    public String promoteToAuthor(@PathVariable Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        user.setRole("ROLE_AUTHOR");
        userRepository.save(user);

        if (authorRepository.findByUser(user) == null) {
            Author authorProfile = new Author();
            authorProfile.setUser(user);
            authorProfile.setName(user.getUsername());
            authorRepository.save(authorProfile);
        }

        return "redirect:/admin/users?promotedToAuthor";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Integer id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users?deleted";
    }
}