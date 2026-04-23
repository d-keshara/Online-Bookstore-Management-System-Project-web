package com.bookstore.online_bookstore.service;

import com.bookstore.online_bookstore.entity.Book;
import com.bookstore.online_bookstore.entity.Order;
import com.bookstore.online_bookstore.entity.OrderItem;
import com.bookstore.online_bookstore.entity.User;
import com.bookstore.online_bookstore.repository.BookRepository;
import com.bookstore.online_bookstore.repository.OrderRepository;
import com.bookstore.online_bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    public List<Order> getOrdersByUsername(String username) {
        return orderRepository.findByUserUsernameOrderByOrderDateDesc(username);
    }

    @Autowired
    private EmailService emailService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Transactional
    public Order placeOrder(Order order) {
        if (order == null || order.getUser() == null || order.getUser().getId() == null) {
            throw new RuntimeException("Order must contain a valid user ID.");
        }
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new RuntimeException("Order must contain at least one item.");
        }

        User fullUser = userRepository.findById(order.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + order.getUser().getId()));

        order.setUser(fullUser);

        BigDecimal totalOrderPrice = BigDecimal.ZERO;

        for (OrderItem item : order.getItems()) {
            if (item.getBookId() == null) {
                throw new RuntimeException("Each order item must include a book ID.");
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new RuntimeException("Item quantity must be greater than zero.");
            }

            Book book = bookRepository.findById(item.getBookId())
                    .orElseThrow(() -> new RuntimeException("Book not found ID: " + item.getBookId()));

            Integer availableStock = book.getStock() == null ? 0 : book.getStock();
            if (availableStock < item.getQuantity()) {
                throw new RuntimeException("Out of stock! Only " + book.getStock() + " copies left of: " + book.getTitle());
            }
            if (book.getPrice() == null) {
                throw new RuntimeException("Book price is missing for book ID: " + item.getBookId());
            }

            book.setStock(availableStock - item.getQuantity());
            bookRepository.save(book);

            item.setOrder(order);

            BigDecimal itemTotalPrice = book.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            item.setPrice(itemTotalPrice);

            totalOrderPrice = totalOrderPrice.add(itemTotalPrice);
        }
        order.setTotalPrice(totalOrderPrice);
        Order savedOrder = orderRepository.save(order);

        // Capture the file path returned by the receipt generator
        String receiptFilePath = generateReceiptFile(savedOrder);

        // Send the email and pass the file path!
        try {
            if (receiptFilePath != null) {
                emailService.sendOrderConfirmation(
                        savedOrder.getUser().getEmail(),
                        savedOrder.getUser().getUsername(),
                        savedOrder.getOrderId(),
                        savedOrder.getTotalPrice(),
                        receiptFilePath
                );
            } else {
                System.err.println("Email skipped because receipt file path was null.");
            }
        } catch (Exception e) {
            System.err.println("Receipt file created, but Email failed: " + e.getMessage());
        }

        return savedOrder;
    }

    //receipt generation

    private String generateReceiptFile(Order order) {
        File folder = new File("files");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String fileName = "files/receipt_order_" + order.getOrderId() + ".txt";

        try (
                FileWriter fileWriter = new FileWriter(fileName);
                PrintWriter printWriter = new PrintWriter(fileWriter)
        ) {
            printWriter.println("==========================================");
            printWriter.println("        NEXUS BOOKSTORE RECEIPT          ");
            printWriter.println("==========================================");
            printWriter.println("Order ID   : " + order.getOrderId());
            printWriter.println("Date       : " + order.getOrderDate());
            printWriter.println("------------------------------------------");

            for (OrderItem item : order.getItems()) {
                BigDecimal unitPrice = BigDecimal.ZERO;
                if (item.getPrice() != null && item.getQuantity() != null && item.getQuantity() > 0) {
                    unitPrice = item.getPrice().divide(BigDecimal.valueOf(item.getQuantity()));
                }

                printWriter.println("Book ID    : " + item.getBookId());
                printWriter.println("Unit Price : LKR " + unitPrice);
                printWriter.println("Quantity   : " + item.getQuantity());
                printWriter.println("Subtotal   : LKR " + item.getPrice());
                printWriter.println("------------------------------------------");
            }

            printWriter.println("TOTAL PAID : LKR " + order.getTotalPrice());
            printWriter.println("==========================================");
            printWriter.println("        Developed by Group WD130          ");
            printWriter.println("==========================================");

            System.out.println(">>> Receipt generated successfully: " + fileName);

            return fileName;

        } catch (IOException e) {
            System.out.println("Error generating receipt file: " + e.getMessage());
            return null;
        }
    }
}
// Final version of Admin and Order management module
