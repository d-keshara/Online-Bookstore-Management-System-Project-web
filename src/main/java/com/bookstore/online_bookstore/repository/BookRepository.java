package com.bookstore.online_bookstore.repository;

import com.bookstore.online_bookstore.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    //Find books where the title contains the keyword
    List<Book> findByTitleContainingIgnoreCase(String keyword);

    // Find books by category
    List<Book> findByCategory(String category);

    List<Book> findByAuthorContainingIgnoreCase(String author);

    List<Book> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
}