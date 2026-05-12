package com.bookstore.online_bookstore.service;

import com.bookstore.online_bookstore.entity.Wishlist;
import com.bookstore.online_bookstore.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    // Save a book to the user's wishlist
    public Wishlist addToWishlist(Wishlist wishlist) {
        return wishlistRepository.save(wishlist);
    }

    // Get all books in a user's wishlist
    // 🔥 THE FIX: Changed Long to Integer, and called the new method name
    public List<Wishlist> getWishlistByUser(Integer userId) {
        return wishlistRepository.findByUser_Id(userId);
    }

    // Remove a book from the wishlist
    // 🔥 THE FIX: Changed Long to Integer, and called the new method name
    @Transactional
    public void removeFromWishlist(Integer userId, Integer bookId) {
        wishlistRepository.deleteByUser_IdAndBook_BookId(userId, bookId);
    }
}