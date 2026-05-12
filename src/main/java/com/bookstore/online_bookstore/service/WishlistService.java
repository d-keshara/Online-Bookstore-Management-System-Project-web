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
        public List<Wishlist> getWishlistByUser(Long userId) {
            return wishlistRepository.findByUserId(userId);
        }

        // Remove a book from the wishlist
        @Transactional
        public void removeFromWishlist(Long userId, Long bookId) {
            wishlistRepository.deleteByUserIdAndBookId(userId, bookId);
        }

}
