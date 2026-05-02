package com.bookstore.online_bookstore.repository;

import com.bookstore.online_bookstore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    @Query("SELECT b FROM Book b JOIN FETCH b.author WHERE LOWER(b.title) LIKE LOWER(concat('%', :keyword, '%')) AND b.approvalStatus = :status")
    Page<Book> findByTitleContainingIgnoreCaseAndApprovalStatus(String keyword, String status, Pageable pageable);

    Page<Book> findByApprovalStatus(String status, Pageable pageable);

    @Query("SELECT b FROM Book b JOIN FETCH b.author WHERE LOWER(b.title) LIKE LOWER(concat('%', :keyword, '%'))")
    Page<Book> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Book> findByCategory(String category, Pageable pageable);

    Page<Book> findByAuthorId(Long authorId, Pageable pageable);

    Page<Book> findByAuthor_NameContainingIgnoreCase(String authorName, Pageable pageable);

    Page<Book> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
}