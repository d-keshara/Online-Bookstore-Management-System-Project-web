package com.bookstore.online_bookstore.repository;

import com.bookstore.online_bookstore.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    // Find all wishlist entries for a specific user
    List<Wishlist> findByUser_Id(Integer userId);

    @Transactional
    void deleteByUser_IdAndBook_BookId(Integer userId, Integer bookId);
}