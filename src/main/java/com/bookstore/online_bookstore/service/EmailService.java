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
            // MimeMessage allows for attachments and rich content
            MimeMessage message = mailSender.createMimeMessage();

            // The "true" flag tells Spring this is a multipart message (meaning it has an attachment)
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("nexusbookstoremailserver@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("Nexus Bookstore - Order Confirmation #" + orderId);

            String body = "Hi "+username+"!\n\nYour order has been placed successfully.\n"
                    + "Order ID: " + orderId + "\n"
                    + "Total: LKR " + totalAmount + "\n\n"
                    + "Please find your official receipt attached to this email.\n\n"
                    + "Thank you for shopping with Nexus!";

            helper.setText(body);

            // Fetch the file from the local "files" folder and attach it
            FileSystemResource file = new FileSystemResource(new File(attachmentFilePath));
            helper.addAttachment("Receipt_Order_" + orderId + ".txt", file);

            mailSender.send(message);
            System.out.println("Email sent successfully with attachment to: " + toEmail);

        } catch (MessagingException e) {
            System.err.println("Failed to send email with attachment: " + e.getMessage());
        }
    }
}