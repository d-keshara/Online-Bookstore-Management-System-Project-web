package com.bookstore.online_bookstore.repository;

import com.bookstore.online_bookstore.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

    @Repository
    public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
        // Find all wishlist entries for a specific user
        List<Wishlist> findByUserId(Long userId);

        // Optional: Find a specific entry to check if it already exists or to delete it
        void deleteByUserIdAndBookId(Long userId, Long bookId);

}
