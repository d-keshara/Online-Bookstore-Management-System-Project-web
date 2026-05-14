package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.Order;
import com.bookstore.online_bookstore.entity.OrderItem;
import com.bookstore.online_bookstore.entity.User;
import com.bookstore.online_bookstore.entity.CartItem;
import com.bookstore.online_bookstore.repository.OrderRepository;
import com.bookstore.online_bookstore.repository.UserRepository;
import com.bookstore.online_bookstore.repository.CartItemRepository;
import com.bookstore.online_bookstore.service.CartService;
import com.bookstore.online_bookstore.service.EmailService;
import com.bookstore.online_bookstore.payment.Payment;
import com.bookstore.online_bookstore.payment.CardPayment;
import com.bookstore.online_bookstore.payment.CashPayment;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderWebController {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CartItemRepository cartItemRepository;

    // UPGRADED RECEIPT GENERATOR TO HANDLE COD DETAILS
    private String generateReceiptText(Order order, String address, String phone) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = LocalDateTime.now().format(formatter);

        String receipt = "==========================================\n" +
                "         NEXUS BOOKSTORE RECEIPT          \n" +
                "==========================================\n" +
                "Order ID       : NX-" + order.getOrderId() + "\n" +
                "Date           : " + formattedDate + "\n" +
                "Payment Method : " + order.getPaymentMethod() + "\n" +
                "Status         : " + order.getStatus() + "\n" +
                "------------------------------------------\n";

        if ("COD".equals(order.getPaymentMethod()) && address != null && !address.isEmpty()) {
            receipt +=   "Delivery To    : " + address + "\n" +
                    "Contact No     : " + phone + "\n" +
                    "------------------------------------------\n";
        }

        receipt +=       "TOTAL PAID     : LKR " + order.getTotalPrice() + "\n" +
                "==========================================\n" +
                "         Thank you for shopping!          \n" +
                "         Developed by Group WD130         \n" +
                "==========================================\n";
        return receipt;
    }

    @GetMapping("/checkout-payment")
    public String showPaymentPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        model.addAttribute("cartTotal", cartService.calculateTotal(user));
        return "payment-selection";
    }

    @PostMapping("/confirm")
    public String confirmOrder(@RequestParam("paymentMethod") String paymentMethod,
                               @RequestParam(value = "deliveryAddress", required = false) String deliveryAddress,
                               @RequestParam(value = "contactNumber", required = false) String contactNumber,
                               @AuthenticationPrincipal UserDetails userDetails,
                               HttpSession session,
                               RedirectAttributes ra) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);

        Order order = new Order();
        order.setUser(user);
        order.setPaymentMethod(paymentMethod);
        Double totalAmount = cartService.calculateTotal(user);
        order.setTotalPrice(BigDecimal.valueOf(totalAmount));

        // THE STRATEGY PATTERN IN ACTION
        Payment paymentStrategy;
        if ("CARD".equals(paymentMethod)) {
            paymentStrategy = new CardPayment();
        } else {
            paymentStrategy = new CashPayment();
        }
        // Triggers the simulated gateway logs in your IntelliJ console!
        paymentStrategy.processPayment(totalAmount);

        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cItem : cartItems) {
            OrderItem oItem = new OrderItem();
            oItem.setOrder(order);
            oItem.setBookId(cItem.getBook().getBookId());
            oItem.setQuantity(cItem.getQuantity());

            BigDecimal itemPrice = cItem.getBook().getPrice().multiply(BigDecimal.valueOf(cItem.getQuantity()));
            oItem.setPrice(itemPrice);

            orderItems.add(oItem);
        }

        order.setItems(orderItems);

        if ("COD".equals(paymentMethod)) {
            order.setStatus("PROCESSING");
            ra.addFlashAttribute("orderSuccess", "Order Placed! Please pay LKR " + order.getTotalPrice() + " on delivery.");
        } else {
            order.setStatus("PAID");
            ra.addFlashAttribute("orderSuccess", "Payment Successful! Your order is being prepared.");
        }

        orderRepository.save(order);

        // Generate the receipt with the dynamic form data
        String receiptContent = generateReceiptText(order, deliveryAddress, contactNumber);

        String directoryPath = "files/";
        String fileName = "receipt_order_" + order.getOrderId() + ".txt";
        String fullPath = directoryPath + fileName;

        try {
            Path path = Paths.get(directoryPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            Files.write(Paths.get(fullPath), receiptContent.getBytes());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                emailService.sendOrderConfirmation(user.getEmail(), user.getUsername(), order.getOrderId(), order.getTotalPrice(), fullPath);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Clear cart and any promo sessions
        cartService.clearCart(user);
        session.removeAttribute("appliedDiscount");
        session.removeAttribute("appliedPromoCode");

        return "redirect:/catalog";
    }

    @PostMapping("/cancel/{id}")
    public String cancelUserOrder(@PathVariable("id") Integer id, RedirectAttributes ra) {
        orderRepository.findById(id).ifPresent(order -> {
            order.setStatus("CANCELLED");
            orderRepository.save(order);
        });
        ra.addFlashAttribute("successMessage", "Order #NX-" + id + " was successfully cancelled.");
        return "redirect:/my-orders";
    }

    @GetMapping("/receipt/{orderId}")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable Integer orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);

        if (order == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        byte[] fileBytes;
        String directoryPath = "files/";
        String fileName = "receipt_order_" + orderId + ".txt";
        Path path = Paths.get(directoryPath + fileName);

        try {
            // Actually read the perfect file we saved during checkout!
            if (Files.exists(path)) {
                fileBytes = Files.readAllBytes(path);
            } else {
                // Fallback ONLY if the file was deleted from the server
                String receiptContent = generateReceiptText(order, "N/A", "N/A");
                fileBytes = receiptContent.getBytes();
            }
        } catch (Exception e) {
            System.out.println("Error reading receipt: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", fileName);

        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }
}