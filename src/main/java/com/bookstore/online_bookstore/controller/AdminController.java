package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.Book;
import com.bookstore.online_bookstore.entity.Order;
import com.bookstore.online_bookstore.repository.BookRepository;
import com.bookstore.online_bookstore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/all-orders")
    public List<Order> viewAllOrders(){
        return orderRepository.findAll();
    }

    @PostMapping("/add-book")
    public ResponseEntity<String> addBook(@RequestBody Book book) {
        bookRepository.save(book);
        return ResponseEntity.ok("Success: New book added to the store inventory!");
    }


    @DeleteMapping("/delete-book/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Integer id) {
        if (!bookRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Error: Book ID does not exist in the database!");
        }

        bookRepository.deleteById(id);
        return ResponseEntity.ok("Success: Book permanently deleted from inventory!");
    }
    @PutMapping("/update-book/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Integer id, @RequestBody Book updatedBook){
        return bookRepository.findById(id).map(book -> {
            book.setTitle(updatedBook.getTitle());
            book.setPrice(updatedBook.getPrice());
            book.setStock(updatedBook.getStock());

            bookRepository.save(book);
            return ResponseEntity.ok("Success: Book ID " + id + " has been updated!");
        })
                .orElse(ResponseEntity.notFound().build());

    }
}