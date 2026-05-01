package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.Book;
import com.bookstore.online_bookstore.entity.User; // Make sure this matches your User entity import
import com.bookstore.online_bookstore.repository.AuthorRepository;
import com.bookstore.online_bookstore.repository.BookRepository;
import com.bookstore.online_bookstore.repository.OrderRepository;
import com.bookstore.online_bookstore.repository.UserRepository; // New import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminWebController {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final UserRepository userRepository; // Added UserRepository

    @Autowired
    public AdminWebController(BookRepository bookRepository, OrderRepository orderRepository, AuthorRepository authorRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
        this.authorRepository = authorRepository;
        this.userRepository = userRepository; // Initialized
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("books", bookRepository.findAll());
        return "admin-dashboard";
    }

    @GetMapping("/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorRepository.findAll());
        return "add-book";
    }

    @PostMapping("/save")
    public String saveBook(@ModelAttribute("book") Book book) {
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

    @GetMapping("/orders")
    public String viewOrders(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "order-history";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin-users";
    }

    @GetMapping("/users/promote/{id}")
    public String promoteToAdmin(@PathVariable Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        user.setRole("ADMIN"); // Upgrades them to an Admin
        userRepository.save(user);
        return "redirect:/admin/users?promoted";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Integer id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users?deleted";
    }
}