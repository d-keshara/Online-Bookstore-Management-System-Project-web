package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.Order;
import com.bookstore.online_bookstore.entity.User;
import com.bookstore.online_bookstore.repository.OrderRepository;
import com.bookstore.online_bookstore.repository.UserRepository;
import com.bookstore.online_bookstore.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/orders")
public class OrderWebController {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartService cartService;

    @GetMapping("/checkout-payment")
    public String showPaymentPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        model.addAttribute("cartTotal", cartService.calculateTotal(user));
        return "payment-selection";
    }

    @PostMapping("/confirm")
    public String confirmOrder(@RequestParam("paymentMethod") String paymentMethod,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes ra) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);

        Order order = new Order();
        order.setUser(user);
        order.setPaymentMethod(paymentMethod);
        order.setTotalPrice(BigDecimal.valueOf(cartService.calculateTotal(user)));

        if ("COD".equals(paymentMethod)) {
            order.setStatus("PROCESSING");
            ra.addFlashAttribute("orderSuccess", "Order Placed! Please pay LKR " + order.getTotalPrice() + " on delivery.");
        } else {
            order.setStatus("PAID");
            ra.addFlashAttribute("orderSuccess", "Payment Successful! Your order is being prepared.");
        }

        orderRepository.save(order);
        cartService.clearCart(user);

        return "redirect:/catalog";
    }
}