package com.bookstore.online_bookstore.repository;

import com.bookstore.online_bookstore.entity.Book;
import com.bookstore.online_bookstore.entity.Review;
import com.bookstore.online_bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    // Find all reviews for a specific book to show on the details page
    List<Review> findByBook_BookId(Integer bookId);
    boolean existsByUserAndBook(User user, Book book);
    List<Review> findByUser(User user);


    @Modifying
    @Transactional
    @Query("DELETE FROM Review r WHERE r.reviewId = :id")
    void forceDeleteById(@Param("id") Integer id);

}

