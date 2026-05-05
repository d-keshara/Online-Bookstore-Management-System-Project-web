package com.bookstore.online_bookstore.service;

import com.bookstore.online_bookstore.dto.OrderRequestDTO;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    public List<Order> getOrdersByUsername(String username) {
        return orderRepository.findByUserUsernameOrderByOrderDateDesc(username);
    }

    @Transactional
    public Order placeOrder(OrderRequestDTO orderRequest) {
        if (orderRequest == null || orderRequest.getUserId() == null) {
            throw new RuntimeException("Order must contain a valid user ID.");
        }
        if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
            throw new RuntimeException("Order must contain at least one item.");
        }

        User fullUser = userRepository.findById(orderRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + orderRequest.getUserId()));

        Order order = new Order();
        order.setUser(fullUser);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalOrderPrice = BigDecimal.ZERO;

        for (OrderRequestDTO.CartItemDTO cartItem : orderRequest.getItems()) {
            Book book = bookRepository.findById(cartItem.getBookId())
                    .orElseThrow(() -> new RuntimeException("Book not found ID: " + cartItem.getBookId()));

            Integer availableStock = book.getStock() == null ? 0 : book.getStock();
            if (availableStock < cartItem.getQuantity()) {
                throw new RuntimeException("Out of stock! Only " + book.getStock() + " copies left of: " + book.getTitle());
            }

            book.setStock(availableStock - cartItem.getQuantity());
            bookRepository.save(book);

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setBookId(cartItem.getBookId());
            item.setQuantity(cartItem.getQuantity());

            BigDecimal itemTotalPrice = book.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            item.setPrice(itemTotalPrice);

            totalOrderPrice = totalOrderPrice.add(itemTotalPrice);
            orderItems.add(item);
        }

        order.setItems(orderItems);
        order.setTotalPrice(totalOrderPrice);
        Order savedOrder = orderRepository.save(order);

        String receiptFilePath = generateReceiptFile(savedOrder);

        try {
            if (receiptFilePath != null) {
                emailService.sendOrderConfirmation(
                        savedOrder.getUser().getEmail(),
                        savedOrder.getUser().getUsername(),
                        savedOrder.getOrderId(),
                        savedOrder.getTotalPrice(),
                        receiptFilePath
                );
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return savedOrder;
    }

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
            printWriter.println("Order ID   : NX-" + order.getOrderId());
            printWriter.println("Date       : " + order.getOrderDate());
            printWriter.println("Payment    : " + (order.getPaymentMethod() != null ? order.getPaymentMethod() : "N/A"));
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

            return fileName;

        } catch (IOException e) {
            return null;
        }
    }
}