package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.Author;
import com.bookstore.online_bookstore.entity.Book;
import com.bookstore.online_bookstore.entity.User;
import com.bookstore.online_bookstore.entity.Order;
import com.bookstore.online_bookstore.entity.OrderItem;
import com.bookstore.online_bookstore.entity.Review;
import com.bookstore.online_bookstore.repository.BookRepository;
import com.bookstore.online_bookstore.repository.UserRepository;
import com.bookstore.online_bookstore.repository.OrderRepository;
import com.bookstore.online_bookstore.repository.ReviewRepository;
import com.bookstore.online_bookstore.service.AuthorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AuthorWebController {

    private final AuthorService authorService;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;

    public AuthorWebController(AuthorService authorService,
                               UserRepository userRepository,
                               BookRepository bookRepository,
                               OrderRepository orderRepository,
                               ReviewRepository reviewRepository) {
        this.authorService = authorService;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/admin/authors/list")
    public String listAuthors(Model model, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Author> authorPage = authorService.getAllAuthors(pageable);

        model.addAttribute("authors", authorPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", authorPage.getTotalPages());

        return "authors-list";
    }

    @GetMapping("/admin/authors/add")
    public String showAuthorForm(Model model) {
        model.addAttribute("author", new Author());
        return "author-form";
    }

    @GetMapping("/admin/authors/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("author", authorService.getAuthorById(id));
        return "author-form";
    }

    @PostMapping("/admin/authors/save")
    public String saveAuthor(@ModelAttribute Author author, RedirectAttributes ra) {
        if (author.getId() == null) {
            authorService.createAuthor(author); // It's a new author
        } else {
            authorService.updateAuthor(author.getId(), author); // It's an edit
        }

        ra.addAttribute("success", true); // Triggers your green alert banner!
        return "redirect:/admin/authors/list";
    }


    @GetMapping("/author/dashboard")
    public String showAuthorDashboard(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        User currentUser = userRepository.findByUsername(principal.getName()).orElse(null);

        if (currentUser != null && currentUser.getAuthor() != null) {
            List<Book> myBooks = bookRepository.findAll().stream()
                    .filter(book -> book.getAuthor() != null &&
                            book.getAuthor().getId().equals(currentUser.getAuthor().getId()))
                    .collect(Collectors.toList());

            int totalBooksSold = 0;
            double totalRating = 0.0;
            int totalReviews = 0;
            List<String> chartLabels = new ArrayList<>();
            List<Integer> chartData = new ArrayList<>();

            List<Order> allOrders = orderRepository.findAll();

            for (Book book : myBooks) {
                if ("APPROVED".equals(book.getApprovalStatus())) {

                    String shortTitle = book.getTitle();
                    if (shortTitle != null && shortTitle.length() > 15) {
                        shortTitle = shortTitle.substring(0, 15) + "...";
                    }
                    chartLabels.add(shortTitle);

                    int bookSales = 0;
                    for (Order order : allOrders) {
                        if (order.getItems() != null) {
                            for (OrderItem item : order.getItems()) {
                                if (item.getBookId() != null && item.getBookId().equals(book.getBookId())) {
                                    int qty = (item.getQuantity() != null && item.getQuantity() > 0) ? item.getQuantity() : 1;
                                    bookSales += qty;
                                }
                            }
                        }
                    }

                    chartData.add(bookSales);
                    totalBooksSold += bookSales;

                    List<Review> reviews = reviewRepository.findByBook_BookId(book.getBookId());
                    for (Review r : reviews) {
                        totalRating += r.getRating();
                        totalReviews++;
                    }
                }
            }

            double averageRating = totalReviews > 0 ? totalRating / totalReviews : 0.0;

            model.addAttribute("books", myBooks);
            model.addAttribute("username", currentUser.getUsername());
            model.addAttribute("totalBooksSold", totalBooksSold);
            model.addAttribute("averageRating", String.format("%.1f", averageRating));
            model.addAttribute("chartLabels", chartLabels);
            model.addAttribute("chartData", chartData);
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