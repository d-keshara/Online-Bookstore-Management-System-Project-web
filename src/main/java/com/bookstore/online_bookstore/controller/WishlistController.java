package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.Wishlist;
import com.bookstore.online_bookstore.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

    @RestController
    @RequestMapping("/api/wishlist")
    public class WishlistController {

        @Autowired
        private WishlistService wishlistService;

        // POST: Save a book for later
        @PostMapping("/add")
        public ResponseEntity<String> addToWishlist(@RequestBody Wishlist wishlist) {
            wishlistService.addToWishlist(wishlist);
            return ResponseEntity.ok("Book added to wishlist successfully!");
        }

        // GET: View "My Wishlist"
        @GetMapping("/{userId}")
        public ResponseEntity<List<Wishlist>> getWishlist(@PathVariable Long userId) {
            List<Wishlist> wishlist = wishlistService.getWishlistByUser(userId);
            return ResponseEntity.ok(wishlist);
        }

        // DELETE: Remove from wishlist
        @DeleteMapping("/remove/{userId}/{bookId}")
        public ResponseEntity<String> removeFromWishlist(@PathVariable Long userId, @PathVariable Long bookId) {
            wishlistService.removeFromWishlist(userId, bookId);
            return ResponseEntity.ok("Book removed from wishlist.");
        }

}
