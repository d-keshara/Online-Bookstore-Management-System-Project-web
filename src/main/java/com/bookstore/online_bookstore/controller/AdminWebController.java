package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.*;
import com.bookstore.online_bookstore.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final ReviewRepository reviewRepository;

    @Autowired
    public AdminWebController(BookRepository bookRepository,
                              OrderRepository orderRepository,
                              AuthorRepository authorRepository,
                              UserRepository userRepository,
                              InviteTokenRepository inviteTokenRepository,
                              ReviewRepository reviewRepository) {
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
        this.authorRepository = authorRepository;
        this.userRepository = userRepository;
        this.inviteTokenRepository = inviteTokenRepository;
        this.reviewRepository = reviewRepository;
    }

    // MAIN DASHBOARD
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        List<Book> approvedBooks = bookRepository.findAll().stream()
                .filter(book -> "APPROVED".equals(book.getApprovalStatus()))
                .collect(Collectors.toList());
        model.addAttribute("books", approvedBooks);

        List<InviteToken> availableTokens = inviteTokenRepository.findAll().stream()
                .filter(token -> !token.isUsed())
                .collect(Collectors.toList());
        model.addAttribute("unusedTokens", availableTokens);

        long pendingCount = bookRepository.findAll().stream()
                .filter(book -> "PENDING".equals(book.getApprovalStatus()))
                .count();
        model.addAttribute("pendingCount", pendingCount);

        // Fetch all NEGATIVE or HIDDEN reviews
        List<Review> urgentReviews = reviewRepository.findAll().stream()
                .filter(review -> "NEGATIVE".equalsIgnoreCase(review.getSentiment()) || review.isHidden())
                .collect(Collectors.toList());
        model.addAttribute("urgentReviews", urgentReviews);

        return "admin-dashboard";
    }

    // ORDER COMMAND CENTER
    @GetMapping("/orders/manage")
    public String manageOrders(Model model) {
        model.addAttribute("allOrders", orderRepository.findAll());
        return "admin-order-management";
    }

    @PostMapping("/orders/update-status")
    public String updateOrderStatus(@RequestParam("orderId") Integer orderId,
                                    @RequestParam("status") String status,
                                    RedirectAttributes ra) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(status);
            orderRepository.save(order);
        });
        ra.addFlashAttribute("successMessage", "Order #" + orderId + " updated to " + status);
        return "redirect:/admin/orders/manage";
    }

    // USER MANAGEMENT
    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin-users";
    }

    @GetMapping("/users/promote/{id}")
    public String promoteToAdmin(@PathVariable Integer id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setRole("ROLE_ADMIN");
            userRepository.save(user);
        });
        return "redirect:/admin/users?promoted";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Integer id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users?deleted";
    }

    //BOOK REQUESTS
    @GetMapping("/requests")
    public String viewBookRequests(Model model) {
        List<Book> pendingBooks = bookRepository.findAll().stream()
                .filter(book -> "PENDING".equals(book.getApprovalStatus()))
                .collect(Collectors.toList());
        model.addAttribute("pendingBooks", pendingBooks);
        return "admin-book-requests";
    }

    // INVENTORY MANAGEMENT
    @GetMapping("/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorRepository.findAll());
        return "add-book";
    }

    @GetMapping("/edit/{id}")
    public String showEditBookForm(@PathVariable Integer id, Model model) {
        bookRepository.findById(id).ifPresent(book -> {
            model.addAttribute("book", book);
            model.addAttribute("authors", authorRepository.findAll());
        });
        return "edit-book";
    }

    @PostMapping("/save")
    public String saveBook(@ModelAttribute("book") Book book,
                           @RequestParam(value = "imageFile", required = false) MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                String uploadDir = "src/main/resources/static/images/";
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path path = Paths.get(uploadDir + fileName);
                Files.write(path, file.getBytes());
                book.setCoverImagePath("/images/" + fileName);
            } catch (IOException e) { e.printStackTrace(); }
        }
        book.setApprovalStatus("APPROVED");
        bookRepository.save(book);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Integer id) {
        bookRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }

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

    // AUTHOR INVITE TOKENS
    @PostMapping("/tokens/generate")
    public String generateNewInviteToken(RedirectAttributes ra) {
        String newCode = "NEXUS-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        InviteToken newToken = new InviteToken();
        newToken.setToken(newCode);
        inviteTokenRepository.save(newToken);
        ra.addFlashAttribute("successMessage", "New author invite code generated: " + newCode);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/tokens/delete/{id}")
    public String deleteInviteToken(@PathVariable Long id, RedirectAttributes ra) {
        inviteTokenRepository.deleteById(id);
        ra.addFlashAttribute("successMessage", "Invite code revoked successfully.");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/requests/review/{id}")
    public String showPublishReviewPage(@PathVariable("id") Integer id, Model model) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) return "redirect:/admin/requests?error=BookNotFound";
        model.addAttribute("book", book);
        return "admin-approve-book";
    }

    @PostMapping("/requests/approve/{id}")
    public String approveBookRequest(@PathVariable("id") Integer id,
                                     @RequestParam("price") java.math.BigDecimal price,
                                     @RequestParam("stock") Integer stock,
                                     RedirectAttributes ra) {
        bookRepository.findById(id).ifPresent(book -> {
            book.setPrice(price);
            book.setStock(stock);
            book.setApprovalStatus("APPROVED");
            bookRepository.save(book);
        });
        ra.addFlashAttribute("successMessage", "Book Published Successfully!");
        return "redirect:/admin/requests";
    }

    @PostMapping("/requests/decline/{id}")
    public String declineBookRequest(@PathVariable("id") Integer id, RedirectAttributes ra) {
        bookRepository.findById(id).ifPresent(book -> {
            book.setApprovalStatus("REJECTED");
            bookRepository.save(book);
        });
        ra.addFlashAttribute("successMessage", "Book request was successfully declined and rejected.");
        return "redirect:/admin/requests";
    }

    // ADMIN REVIEW MANAGEMENT ENDPOINTS
    @PostMapping("/reviews/delete/{id}")
    public String deleteReview(@PathVariable Integer id, RedirectAttributes ra) {
        reviewRepository.deleteById(id);
        ra.addFlashAttribute("successMessage", "Review was permanently deleted from the database.");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/reviews/unhide/{id}")
    public String unhideReview(@PathVariable Integer id, RedirectAttributes ra) {
        reviewRepository.findById(id).ifPresent(review -> {
            review.setHidden(false);
            reviewRepository.save(review);
        });
        ra.addFlashAttribute("successMessage", "Review has been unhidden and is now public.");
        return "redirect:/admin/dashboard";
    }

    // NEW: Manual Hide Endpoint
    @PostMapping("/reviews/hide/{id}")
    public String hideReview(@PathVariable Integer id, RedirectAttributes ra) {
        reviewRepository.findById(id).ifPresent(review -> {
            review.setHidden(true);
            reviewRepository.save(review);
        });
        ra.addFlashAttribute("successMessage", "Review has been manually hidden from the public.");
        return "redirect:/admin/dashboard";
    }
}