package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.Book;
import com.bookstore.online_bookstore.repository.BookRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        return ResponseEntity.ok(bookRepository.save(book));
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookRepository.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String keyword) {
        List<Book> results = bookRepository.findByTitleContainingIgnoreCase(keyword);
        if (results.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(results);
    }

    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<Book>> getBooksByCategory(@PathVariable String categoryName) {
        List<Book> books = bookRepository.findByCategory(categoryName);

        if (books.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(books);
    }

    @GetMapping("/author/{authorName}")
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable String authorName) {
        List<Book> books = bookRepository.findByAuthor_NameContainingIgnoreCase(authorName);

        if (books.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(books);
    }

    @GetMapping("/price")
    public ResponseEntity<List<Book>> getBooksByPriceRange(@RequestParam BigDecimal min,
                                                           @RequestParam BigDecimal max) {
        List<Book> books = bookRepository.findByPriceBetween(min, max);

        if (books.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(books);
    }
}