package com.bookstore.online_bookstore.service;

import java.io.File;
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

        User fullUser = userRepository.findById(order.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + order.getUser().getId()));

        order.setUser(fullUser);

        double totalOrderPrice = 0;

        for (OrderItem item : order.getItems()) {

            Book book = bookRepository.findById(item.getBookId())
                    .orElseThrow(() -> new RuntimeException("Book not found ID: " + item.getBookId()));

            if (book.getStock() < item.getQuantity()) {
                throw new RuntimeException("Out of stock! Only " + book.getStock() + " copies left of: " + book.getTitle());
            }

            book.setStock(book.getStock() - item.getQuantity());
            bookRepository.save(book);

            item.setOrder(order);

            double itemTotalPrice = book.getPrice() * item.getQuantity();
            item.setPrice(itemTotalPrice);

            totalOrderPrice += itemTotalPrice;
        }
        order.setTotalPrice(totalOrderPrice);
        Order savedOrder = orderRepository.save(order);

        generateReceiptFile(savedOrder);


        try {
            emailService.sendOrderConfirmation(
                    savedOrder.getUser().getEmail(),
                    savedOrder.getOrderId(),
                    savedOrder.getTotalPrice()
            );
        } catch (Exception e) {
            System.err.println("Receipt file created, but Email failed: " + e.getMessage());
        }

        return savedOrder;


    }

    //receipt generation

    private void generateReceiptFile(Order order) {

        File folder = new File("files");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        try {
            // Check if the 'files' directory exists, if not create it
            File directory = new File("files");
            if (!directory.exists()) {
                directory.mkdir();
            }

            String fileName = "files/receipt_order_" + order.getOrderId() + ".txt";
            FileWriter fileWriter = new FileWriter(fileName);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            printWriter.println("==========================================");
            printWriter.println("        NEXUS BOOKSTORE RECEIPT          ");
            printWriter.println("==========================================");
            printWriter.println("Order ID   : " + order.getOrderId());
            printWriter.println("Date       : " + order.getOrderDate());
            printWriter.println("------------------------------------------");

            for (OrderItem item : order.getItems()) {
                double unitPrice = item.getPrice()/ item.getQuantity();

                printWriter.println("Book ID    : " + item.getBookId());
                printWriter.println("Unit Price : LKR " + unitPrice);
                printWriter.println("Quantity   : " + item.getQuantity());
                printWriter.println("Subtotal   : LKR " + item.getPrice());
                printWriter.println("------------------------------------------");
            }

            printWriter.println("TOTAL PAID : LKR " + order.getTotalPrice());
            printWriter.println("==========================================");
            printWriter.println("   Developed by Keshara - Group WD130      ");
            printWriter.println("==========================================");

            printWriter.close();
            System.out.println(">>> Receipt generated successfully: " + fileName);

        } catch (IOException e) {
            System.out.println("Error generating receipt file: " + e.getMessage());
        }
    }
}
// Final version of Admin and Order management module