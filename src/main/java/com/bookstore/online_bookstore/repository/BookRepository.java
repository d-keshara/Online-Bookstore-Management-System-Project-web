package com.bookstore.online_bookstore.repository;

import com.bookstore.online_bookstore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    // Find books where the title contains the keyword
    @Query("SELECT b FROM Book b JOIN FETCH b.author WHERE LOWER(b.title) LIKE LOWER(concat('%', :keyword, '%'))")
    Page<Book> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    // Find books by category
    Page<Book> findByCategory(String category, Pageable pageable);

    // Find books by author id
    Page<Book> findByAuthorId(Long authorId, Pageable pageable);

    // Find books by author name
    Page<Book> findByAuthor_NameContainingIgnoreCase(String authorName, Pageable pageable);

    // Find books by price range
    Page<Book> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
}