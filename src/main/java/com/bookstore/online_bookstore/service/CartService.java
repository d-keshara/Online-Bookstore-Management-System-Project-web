package com.bookstore.online_bookstore.service;

import com.bookstore.online_bookstore.entity.Book;
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

    public void addToCart(User user, Book book, int quantity) {
        CartItem existingItem = cartItemRepository.findByUserAndBook(user, book);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setUser(user);
            newItem.setBook(book);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }
    }

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