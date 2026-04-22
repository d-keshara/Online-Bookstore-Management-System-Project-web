package com.bookstore.online_bookstore.repository;

import com.bookstore.online_bookstore.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    @SuppressWarnings("unused")
    List<Author> findByNameContainingIgnoreCase(String name);
}