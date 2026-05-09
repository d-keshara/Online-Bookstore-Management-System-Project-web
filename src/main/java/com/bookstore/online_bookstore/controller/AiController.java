package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.Book;
import com.bookstore.online_bookstore.repository.BookRepository;
import com.bookstore.online_bookstore.service.OllamaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private OllamaService ollamaService;

    @Autowired
    private BookRepository bookRepository;

    @PostMapping("/enhance-synopsis")
    public ResponseEntity<?> enhanceSynopsis(@RequestBody Map<String, String> payload) {
        String draft = payload.get("draft");
        if (draft == null || draft.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Draft cannot be empty"));
        }

        // Call Ollama Service!
        String enhancedText = ollamaService.enhanceSynopsis(draft);

        return ResponseEntity.ok(Map.of("enhanced", enhancedText));
    }

    // The Strict Chatbot Endpoint (Reads Database)
    @PostMapping("/chat")
    public ResponseEntity<?> chatWithAI(@RequestBody Map<String, String> request) {
        String userQuery = request.get("query");

        List<Book> books = bookRepository.findAll().stream()
                .filter(b -> "APPROVED".equals(b.getApprovalStatus()))
                .toList();

        StringBuilder catalogData = new StringBuilder();
        for (Book book : books) {
            catalogData.append(book.getTitle()).append(" (LKR ").append(book.getPrice()).append("), ");
        }

        String aiResponse = ollamaService.chatWithDatabase(userQuery, catalogData.toString());

        return ResponseEntity.ok(Map.of("answer", aiResponse));
    }

    // AI Apology Email Generator
    @PostMapping("/apology")
    public ResponseEntity<?> generateApology(@RequestBody Map<String, String> request) {
        String customerName = request.get("customerName");
        String bookTitle = request.get("bookTitle");
        String reviewText = request.get("reviewText");

        String email = ollamaService.generateApologyEmail(customerName, bookTitle, reviewText);

        return ResponseEntity.ok(Map.of("email", email));
    }
}