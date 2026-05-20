package com.bookstore.online_bookstore.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOrderConfirmation(String toEmail, String username, Integer orderId, BigDecimal totalAmount, String attachmentFilePath) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("nexusbookstoremailserver@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("Nexus Bookstore - Order Confirmation #NX-" + orderId);

            String body = "Hi " + username + "!\n\nYour order has been placed successfully.\n"
                    + "Order ID: NX-" + orderId + "\n"
                    + "Total: LKR " + totalAmount + "\n\n"
                    + "Please find your official receipt attached to this email.\n\n"
                    + "Thank you for shopping with Nexus!";

            helper.setText(body);

            // Fetch the physical file from your local folder and attach it
            FileSystemResource file = new FileSystemResource(new File(attachmentFilePath));
            helper.addAttachment("receipt_order_" + orderId + ".txt", file);

            mailSender.send(message);
            System.out.println("Email sent successfully with attachment to: " + toEmail);

        } catch (MessagingException e) {
            System.err.println("Failed to send email with attachment: " + e.getMessage());
        }
    }

    // Sends a live update whenever the Admin changes the order status!
    public void sendOrderStatusUpdate(String toEmail, String username, Integer orderId, String newStatus) {
        try {
            org.springframework.mail.SimpleMailMessage message = new org.springframework.mail.SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Order Update: #NX-" + orderId + " is now " + newStatus);
            message.setText("Hello " + username + ",\n\n" +
                    "Good news! There is an update regarding your recent purchase.\n\n" +
                    "Order Reference : #NX-" + orderId + "\n" +
                    "Current Status  : " + newStatus + "\n\n" +
                    "You can view your updated receipt by logging into your Nexus Bookstore account and visiting 'My Orders'.\n\n" +
                    "Thank you for shopping with us!\n" +
                    "- The Nexus Team");

            mailSender.send(message);
            System.out.println("Status update email sent to: " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send Order Status Update Email: " + e.getMessage());
        }
    }
}