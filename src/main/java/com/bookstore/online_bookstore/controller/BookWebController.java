package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.Book;
import com.bookstore.online_bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BookWebController {

    private final BookRepository bookRepository;
    private final com.bookstore.online_bookstore.repository.OrderRepository orderRepository;
    private final com.bookstore.online_bookstore.repository.UserRepository userRepository;

    public BookWebController(BookRepository bookRepository,
                             com.bookstore.online_bookstore.repository.OrderRepository orderRepository,
                             com.bookstore.online_bookstore.repository.UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/catalog")
    public String viewCatalog(Model model, @RequestParam(required = false) String keyword) {
        List<Book> books;

        if (keyword != null && !keyword.isEmpty()) {
            books = bookRepository.findByTitleContainingIgnoreCase(keyword);
        } else {
            books = bookRepository.findAll();
        }

        model.addAttribute("books", books);
        return "book-catalog";
    }
    @PostMapping("/buy")
    public String buyBook(@RequestParam("bookId") Integer bookId, java.security.Principal principal) {
        if (principal == null) return "redirect:/login";

        Book book = bookRepository.findById(bookId).orElse(null);
        String username = principal.getName();
        com.bookstore.online_bookstore.entity.User user = userRepository.findByUsername(username).orElse(null);

        if (book != null && user != null) {
            // 1. Save to Database
            com.bookstore.online_bookstore.entity.Order order = new com.bookstore.online_bookstore.entity.Order();
            order.setUser(user);
            order.setTotalPrice(book.getPrice());
            order.setOrderDate(java.time.LocalDateTime.now());
            order.setStatus("PAID");
            orderRepository.save(order);


            java.io.File directory = new java.io.File("files");
            if (!directory.exists()) {
                directory.mkdir();
            }


            String fileName = "files/receipt_order_" + order.getOrderId() + ".txt";

            try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(fileName))) {
                bw.write("========================================");
                bw.newLine();
                bw.write("        NEXUS BOOKSTORE RECEIPT         ");
                bw.newLine();
                bw.write("========================================");
                bw.newLine();
                bw.write("Order ID    : " + order.getOrderId());
                bw.newLine();
                bw.write("Date        : " + java.time.LocalDateTime.now());
                bw.newLine();
                bw.write("----------------------------------------");
                bw.newLine();
                bw.write("Book ID     : " + book.getBookId());
                bw.newLine();
                bw.write("Unit Price  : LKR " + book.getPrice());
                bw.newLine();
                bw.write("Quantity    : 1"); 
                bw.newLine();
                bw.write("Subtotal    : LKR " + book.getPrice());
                bw.newLine();
                bw.write("----------------------------------------");
                bw.newLine();
                bw.write("TOTAL PAID  : LKR " + book.getPrice());
                bw.newLine();
                bw.write("========================================");
                bw.newLine();
                bw.write("   Developed by Keshara - Group WD130   ");
                bw.newLine();
                bw.write("========================================");

                bw.flush();
                System.out.println("NEW RECEIPT CREATED: " + fileName);
            } catch (java.io.IOException e) {
                System.err.println("File Error: " + e.getMessage());
            }

            return "redirect:/catalog?success";
        }
        return "redirect:/catalog?error";
    }
}