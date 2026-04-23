package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.Book;
import com.bookstore.online_bookstore.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

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

    // 1. Get All Books (Paginated)
    @GetMapping
    public ResponseEntity<Page<Book>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(bookRepository.findAll(pageable));
    }

    // 2. Search by Title (Paginated)
    @GetMapping("/search")
    public ResponseEntity<Page<Book>> searchBooks(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> results = bookRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        return ResponseEntity.ok(results);
    }

    // 3. Filter by Category (Paginated)
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<Page<Book>> getBooksByCategory(
            @PathVariable String categoryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookRepository.findByCategory(categoryName, pageable);
        return ResponseEntity.ok(books);
    }

    // 4. Filter by Author (Paginated)
    @GetMapping("/author/{authorName}")
    public ResponseEntity<Page<Book>> getBooksByAuthor(
            @PathVariable String authorName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookRepository.findByAuthor_NameContainingIgnoreCase(authorName, pageable);
        return ResponseEntity.ok(books);
    }

    // 5. Filter by Price Range (Paginated)
    @GetMapping("/price")
    public ResponseEntity<Page<Book>> getBooksByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("price").ascending());
        Page<Book> books = bookRepository.findByPriceBetween(min, max, pageable);
        return ResponseEntity.ok(books);
    }
}