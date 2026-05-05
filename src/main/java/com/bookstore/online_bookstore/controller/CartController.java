package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.dto.OrderRequestDTO;
import com.bookstore.online_bookstore.entity.Book;
import com.bookstore.online_bookstore.entity.CartItem;
import com.bookstore.online_bookstore.entity.User;
import com.bookstore.online_bookstore.repository.BookRepository;
import com.bookstore.online_bookstore.repository.CartItemRepository;
import com.bookstore.online_bookstore.repository.UserRepository;
import com.bookstore.online_bookstore.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final OrderService orderService;

    public CartController(CartItemRepository cartItemRepository, BookRepository bookRepository,
                          UserRepository userRepository, OrderService orderService) {
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.orderService = orderService;
    }

    @GetMapping
    public String viewCart(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user == null) return "redirect:/login";

        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            total = total.add(item.getBook().getPrice().multiply(new BigDecimal(item.getQuantity())));
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", total);
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("bookId") Integer bookId, Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        Book book = bookRepository.findById(bookId).orElse(null);

        if (user != null && book != null) {
            CartItem existingItem = cartItemRepository.findByUserAndBook(user, book);
            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + 1);
                cartItemRepository.save(existingItem);
            } else {
                CartItem newItem = new CartItem();
                newItem.setUser(user);
                newItem.setBook(book);
                newItem.setQuantity(1);
                cartItemRepository.save(newItem);
            }
        }
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam("itemId") Integer itemId, @RequestParam("action") String action) {
        CartItem item = cartItemRepository.findById(itemId).orElse(null);
        if (item != null) {
            if ("increase".equals(action)) {
                item.setQuantity(item.getQuantity() + 1);
                cartItemRepository.save(item);
            } else if ("decrease".equals(action)) {
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                    cartItemRepository.save(item);
                } else {
                    cartItemRepository.delete(item);
                }
            }
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam("itemId") Integer itemId) {
        cartItemRepository.deleteById(itemId);
        return "redirect:/cart";
    }

    @Transactional
    @PostMapping("/checkout")
    public String checkoutCart(Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user == null) return "redirect:/login";

        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) return "redirect:/cart?empty";

        return "redirect:/orders/checkout-payment";
    }
}