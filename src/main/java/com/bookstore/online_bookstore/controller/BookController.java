package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.Book;
import com.bookstore.online_bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books") // Unifies all endpoints in this file under /api/books
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    //ADMIN ENDPOINT: Add a new book
    //URL: POST http://localhost:8080/api/books
    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        return ResponseEntity.ok(bookRepository.save(book));
    }

    //PUBLIC ENDPOINT: Get all books
    //URL: GET http://localhost:8080/api/books

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookRepository.findAll());
    }

    //PUBLIC ENDPOINT: Search by Title
    //URL: GET http://localhost:8080/api/books/search?keyword=java

    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String keyword) {
        List<Book> results = bookRepository.findByTitleContainingIgnoreCase(keyword);
        if (results.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(results);
    }

    // PUBLIC ENDPOINT: Filter by Category
    // URL: GET http://localhost:8080/api/books/category/Fiction

    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<Book>> getBooksByCategory(@PathVariable String categoryName) {
        List<Book> books = bookRepository.findByCategory(categoryName);

        if (books.isEmpty()) {
            return ResponseEntity.noContent().build(); //Returns 204 No Content instead of []
        }

        return ResponseEntity.ok(books);
    }
}