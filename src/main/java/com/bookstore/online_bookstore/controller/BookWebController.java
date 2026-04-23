package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.Book;
import com.bookstore.online_bookstore.entity.Order;
import com.bookstore.online_bookstore.entity.OrderItem;
import com.bookstore.online_bookstore.entity.User;
import com.bookstore.online_bookstore.repository.BookRepository;
import com.bookstore.online_bookstore.repository.UserRepository;
import com.bookstore.online_bookstore.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BookWebController {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final OrderService orderService; // Use the service we already improved!

    public BookWebController(BookRepository bookRepository,
                             UserRepository userRepository,
                             OrderService orderService) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.orderService = orderService;
    }

    @GetMapping("/catalog")
    public String viewCatalog(Model model,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(defaultValue = "0") int page) {

        // Define page size (e.g., 8 books look good on a web grid)
        Pageable pageable = PageRequest.of(page, 8);
        Page<Book> bookPage;

        if (keyword != null && !keyword.isEmpty()) {
            bookPage = bookRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        } else {
            bookPage = bookRepository.findAll(pageable);
        }

        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("keyword", keyword);

        return "book-catalog";
    }

    @PostMapping("/buy")
    public String buyBook(@RequestParam("bookId") Integer bookId, Principal principal) {
        if (principal == null) return "redirect:/login";

        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        Book book = bookRepository.findById(bookId).orElse(null);

        if (book != null && user != null) {
            // Instead of manual file writing here, we use the OrderService!
            // This ensures the stock is updated, the receipt is saved, and the email is sent.

            Order order = new Order();
            order.setUser(user);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus("PAID");

            OrderItem item = new OrderItem();
            item.setBookId(bookId);
            item.setQuantity(1);
            item.setOrder(order);

            List<OrderItem> items = new ArrayList<>();
            items.add(item);
            order.setItems(items);

            try {
                // This one call handles everything we built earlier!
                orderService.placeOrder(order);
                return "redirect:/catalog?success";
            } catch (Exception e) {
                System.err.println("Order Error: " + e.getMessage());
                return "redirect:/catalog?error";
            }
        }
        return "redirect:/catalog?error";
    }
}