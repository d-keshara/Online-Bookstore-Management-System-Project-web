package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.dto.OrderRequestDTO;
import com.bookstore.online_bookstore.entity.Book;
import com.bookstore.online_bookstore.entity.Review;
import com.bookstore.online_bookstore.entity.User;
import com.bookstore.online_bookstore.repository.BookRepository;
import com.bookstore.online_bookstore.repository.ReviewRepository;
import com.bookstore.online_bookstore.repository.UserRepository;
import com.bookstore.online_bookstore.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime; // 🌟 IMPORTED DATE/TIME
import java.util.ArrayList;
import java.util.List;

@Controller
public class BookWebController {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final OrderService orderService;
    private final ReviewRepository reviewRepository;

    public BookWebController(BookRepository bookRepository,
                             UserRepository userRepository,
                             OrderService orderService,
                             ReviewRepository reviewRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.orderService = orderService;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/catalog")
    public String viewCatalog(Model model,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(required = false) String category,
                              @RequestParam(required = false, defaultValue = "newest") String sort,
                              @RequestParam(defaultValue = "0") int page) {

        Sort jpaSort = Sort.by(Sort.Direction.DESC, "bookId");

        if ("price_asc".equals(sort)) {
            jpaSort = Sort.by(Sort.Direction.ASC, "price");
        } else if ("price_desc".equals(sort)) {
            jpaSort = Sort.by(Sort.Direction.DESC, "price");
        }

        Pageable pageable = PageRequest.of(page, 10, jpaSort);
        Page<Book> bookPage;

        if (keyword != null && !keyword.isEmpty()) {
            bookPage = bookRepository.findByTitleContainingIgnoreCaseAndApprovalStatus(keyword, "APPROVED", pageable);
        } else if (category != null && !category.isEmpty()) {
            bookPage = bookRepository.findByCategoryAndApprovalStatus(category, "APPROVED", pageable);
        } else {
            bookPage = bookRepository.findByApprovalStatus("APPROVED", pageable);
        }

        model.addAttribute("books", bookPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("currentSort", sort);

        return "book-catalog";
    }

    @PostMapping("/buy")
    public String buyBook(@RequestParam("bookId") Integer bookId, Principal principal) {
        if (principal == null) return "redirect:/login";

        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        Book book = bookRepository.findById(bookId).orElse(null);

        if (book != null && user != null) {
            OrderRequestDTO orderRequest = new OrderRequestDTO();
            orderRequest.setUserId(Integer.valueOf(user.getId().toString()));

            OrderRequestDTO.CartItemDTO cartItem = new OrderRequestDTO.CartItemDTO();
            cartItem.setBookId(bookId);
            cartItem.setQuantity(1);

            List<OrderRequestDTO.CartItemDTO> cartItems = new ArrayList<>();
            cartItems.add(cartItem);
            orderRequest.setItems(cartItems);

            try {
                orderService.placeOrder(orderRequest);
                return "redirect:/catalog?success";
            } catch (Exception e) {
                return "redirect:/catalog?error=" + e.getMessage();
            }
        }
        return "redirect:/catalog?error";
    }

    @GetMapping("/details")
    public String viewBookDetails(@RequestParam("id") Integer id, Model model) {
        Book book = bookRepository.findById(id).orElse(null);

        if (book == null) {
            return "redirect:/catalog?error=BookNotFound";
        }

        List<Review> reviews = reviewRepository.findByBook_BookId(book.getBookId());

        model.addAttribute("book", book);
        model.addAttribute("reviews", reviews);

        return "details";
    }

    @PostMapping("/reviews/add")
    public String addReview(@RequestParam("bookId") Integer bookId,
                            @RequestParam("rating") Integer rating,
                            @RequestParam("comment") String comment,
                            Principal principal) {

        if (principal == null) return "redirect:/login";

        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        Book book = bookRepository.findById(bookId).orElse(null);

        if (user != null && book != null) {
            boolean alreadyReviewed = reviewRepository.existsByUserAndBook(user, book);

            if (alreadyReviewed) {
                return "redirect:/details?id=" + bookId + "&review_error=true";
            }

            Review review = new Review();
            review.setBook(book);
            review.setUser(user);
            review.setRating(rating);
            review.setComment(comment);

            review.setCreatedAt(LocalDateTime.now());

            reviewRepository.save(review);

            return "redirect:/details?id=" + bookId + "&review_success=true";
        }

        return "redirect:/catalog";
    }
}