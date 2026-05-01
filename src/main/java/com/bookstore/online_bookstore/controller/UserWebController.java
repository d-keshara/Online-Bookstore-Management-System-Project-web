package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.Book;
import com.bookstore.online_bookstore.entity.Order;
import com.bookstore.online_bookstore.entity.User;
import com.bookstore.online_bookstore.repository.BookRepository;
import com.bookstore.online_bookstore.repository.UserRepository;
import com.bookstore.online_bookstore.service.OrderService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class UserWebController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookRepository bookRepository;

    public UserWebController(OrderService orderService,
                             UserRepository userRepository,
                             PasswordEncoder passwordEncoder,
                             BookRepository bookRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.bookRepository = bookRepository;
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

    @PostMapping("/register/save")
    public String saveNewUser(@ModelAttribute("user") User user) {
        user.setRole("USER");

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        return "redirect:/login?registered=true";
    }
}