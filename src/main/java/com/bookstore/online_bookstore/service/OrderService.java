package com.bookstore.online_bookstore.service;

import com.bookstore.online_bookstore.payment.Payment;
import com.bookstore.online_bookstore.payment.CardPayment;
import com.bookstore.online_bookstore.payment.CashPayment;

import com.bookstore.online_bookstore.entity.Book;
import com.bookstore.online_bookstore.entity.Order;
import com.bookstore.online_bookstore.entity.OrderItem;
import com.bookstore.online_bookstore.repository.BookRepository;
import com.bookstore.online_bookstore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BookRepository bookRepository;

    @Transactional
    public Order placeOrder(Order order) {

        if (order.getItems() != null){
           for (OrderItem item : order.getItems()){

               Book book = bookRepository.findById(item.getBookId())
                       .orElseThrow(() -> new RuntimeException("Book ID "+item.getBookId() + " not found!"));

               if (book.getStock() < item.getQuantity()) {
                   throw new RuntimeException("Not enough stock for book: " + book.getTitle());
               }

               book.setStock(book.getStock() - item.getQuantity());
               bookRepository.save(book);

               item.setOrder(order);
           }
       }
        Payment paymentProcessor;
        if ("Card".equalsIgnoreCase(order.getPaymentMethod())) {
            paymentProcessor = new CardPayment();
        }
        else{
            paymentProcessor = new CashPayment();
        }

        paymentProcessor.processPayment(order.getTotalPrice());

        Order saveOrder = orderRepository.save(order);
        generateReceipt(saveOrder);
        return saveOrder;
    }

    private void generateReceipt(Order order) {
        File directory = new File("files");
        if (!directory.exists()) {
            directory.mkdir();
        }

        try (FileWriter writer = new FileWriter("files/receipt.txt", true)) {
            writer.write("Order ID: " + order.getOrderId() + "\n");
            writer.write("Total Price: Rs. " + order.getTotalPrice() + "\n");
            writer.write("Status: SUCCESS\n");
            writer.write("-------------------------------\n");
        } catch (IOException e) {
            System.out.println("Could not generate receipt: " + e.getMessage());
        }
    }
}