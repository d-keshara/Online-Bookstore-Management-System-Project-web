package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.Author;
import com.bookstore.online_bookstore.entity.Book;
import com.bookstore.online_bookstore.service.AuthorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    public ResponseEntity<Author> createAuthor(@RequestBody Author author) {
        return new ResponseEntity<>(authorService.createAuthor(author), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<Author>> getAllAuthors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {


        Pageable pageable = PageRequest.of(page, size);

        Page<Author> authors = authorService.getAllAuthors(pageable);

        return new ResponseEntity<>(authors, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @GetMapping("/{id}/books")
    public ResponseEntity<Page<Book>> getAuthorBooks(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Create the pageable object
        Pageable pageable = PageRequest.of(page, size);

        // Pass it down to the service
        Page<Book> books = authorService.getBooksByAuthorId(id, pageable);

        return ResponseEntity.ok(books);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Author> updateAuthor(@PathVariable Long id, @RequestBody Author author) {
        return ResponseEntity.ok(authorService.updateAuthor(id, author));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.ok("Author deleted successfully");
    }
}