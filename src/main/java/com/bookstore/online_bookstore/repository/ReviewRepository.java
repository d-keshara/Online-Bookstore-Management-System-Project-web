package com.bookstore.online_bookstore.repository;

import com.bookstore.online_bookstore.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    // Find all reviews for a specific book to show on the details page
    List<Review> findByBook_BookId(Integer bookId);
}
