package com.bookstore.online_bookstore.service;

import com.bookstore.online_bookstore.entity.CartItem;
import com.bookstore.online_bookstore.entity.User;
import com.bookstore.online_bookstore.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    public double calculateTotal(User user) {
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        return cartItems.stream()
                .mapToDouble(item -> item.getBook().getPrice().doubleValue() * item.getQuantity())
                .sum();
    }

    @Transactional
    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }
}