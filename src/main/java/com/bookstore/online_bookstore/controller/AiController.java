package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.Book;
import com.bookstore.online_bookstore.entity.Review;
import com.bookstore.online_bookstore.repository.BookRepository;
import com.bookstore.online_bookstore.repository.ReviewRepository;
import com.bookstore.online_bookstore.service.OllamaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private OllamaService ollamaService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @PostMapping("/enhance-synopsis")
    public ResponseEntity<?> enhanceSynopsis(@RequestBody Map<String, String> payload) {
        String draft = payload.get("draft");
        if (draft == null || draft.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Draft cannot be empty"));
        }

        String enhancedText = ollamaService.enhanceSynopsis(draft);
        return ResponseEntity.ok(Map.of("enhanced", enhancedText));
    }

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

    @PostMapping("/apology")
    public ResponseEntity<?> generateApology(@RequestBody Map<String, String> request) {
        String customerName = request.get("customerName");
        String bookTitle = request.get("bookTitle");
        String reviewText = request.get("reviewText");

        String email = ollamaService.generateApologyEmail(customerName, bookTitle, reviewText);

        return ResponseEntity.ok(Map.of("email", email));
    }

    // THE DATA SHIELD
    @PostMapping("/review-summary")
    public ResponseEntity<?> summarizeReviews(@RequestBody Map<String, Integer> request) {
        Integer bookId = request.get("bookId");

        List<Review> reviews = reviewRepository.findByBook_BookId(bookId).stream()
                .filter(r -> !r.isHidden())
                .toList();

        //Check if there are enough actual reviews
        if (reviews.size() < 3) {
            return ResponseEntity.ok(Map.of("summary", "Not enough detailed reviews to form an AI consensus yet. Be the first to leave an in-depth review!"));
        }

        StringBuilder allReviewsText = new StringBuilder();
        for (Review r : reviews) {
            allReviewsText.append(r.getComment()).append(" | ");
        }

        //Check if the reviews are just 1-word answers (Data Starvation)
        if (allReviewsText.length() < 5) {
            return ResponseEntity.ok(Map.of("summary", "Current reviews are too brief for an AI consensus."));
        }

        String summary = ollamaService.summarizeBookReviews(allReviewsText.toString());
        return ResponseEntity.ok(Map.of("summary", summary));
    }
}