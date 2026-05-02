package com.bookstore.online_bookstore.repository;

import com.bookstore.online_bookstore.entity.Author;
import com.bookstore.online_bookstore.entity.User; // New Import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    List<Author> findByNameContainingIgnoreCase(String name);

    // NEW: Find profile by user account
    Author findByUser(User user);
}