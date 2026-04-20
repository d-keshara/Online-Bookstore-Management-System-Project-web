package com.bookstore.online_bookstore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOrderConfirmation(String toEmail, Integer orderId, Double totalAmount) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("nexusbookstoremailserver@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Nexus Bookstore - Order Confirmation #" + orderId);

        String body = "Hi there!\n\nYour order has been placed successfully.\n"
                + "Order ID: " + orderId + "\n"
                + "Total: LKR " + totalAmount + "\n\n"
                + "Thank you for shopping with Nexus!";

        message.setText(body);
        mailSender.send(message);
        System.out.println("Email sent successfully to: " + toEmail);
    }
}