package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.Book;
import com.bookstore.online_bookstore.entity.User;
import com.bookstore.online_bookstore.entity.Wishlist;
import com.bookstore.online_bookstore.repository.BookRepository;
import com.bookstore.online_bookstore.repository.UserRepository;
import com.bookstore.online_bookstore.service.WishlistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public WishlistController(WishlistService wishlistService, UserRepository userRepository, BookRepository bookRepository) {
        this.wishlistService = wishlistService;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public String viewWishlist(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user == null) return "redirect:/login";

        List<Wishlist> items = wishlistService.getWishlistByUser(Integer.valueOf(user.getId().toString()));
        model.addAttribute("wishlistItems", items);
        return "wishlist";
    }

    //THE SMART TOGGLE LOGIC
    @PostMapping("/toggle")
    public String toggleWishlist(@RequestParam("bookId") Integer bookId, Principal principal, RedirectAttributes ra, jakarta.servlet.http.HttpServletRequest request) {
        if (principal == null) return "redirect:/login";

        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        Book book = bookRepository.findById(bookId).orElse(null);

        if (user != null && book != null) {
            List<Wishlist> existingList = wishlistService.getWishlistByUser(Integer.valueOf(user.getId().toString()));
            boolean alreadyExists = existingList.stream().anyMatch(w -> w.getBook().getBookId().equals(bookId));

            if (alreadyExists) {
                wishlistService.removeFromWishlist(Integer.valueOf(user.getId().toString()), bookId);
                ra.addFlashAttribute("wishlistInfo", "💔 '" + book.getTitle() + "' removed from your Wishlist.");
            } else {
                Wishlist wishlist = new Wishlist(user, book);
                wishlistService.addToWishlist(wishlist);
                ra.addFlashAttribute("wishlistSuccess", "❤️ '" + book.getTitle() + "' added to your Wishlist!");
            }
        }

        // SEND THEM RIGHT BACK TO THE DETAILS PAGE
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/catalog");
    }

    @PostMapping("/remove")
    public String removeFromWishlist(@RequestParam("bookId") Integer bookId, Principal principal, RedirectAttributes ra) {
        if (principal == null) return "redirect:/login";

        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user != null) {
            wishlistService.removeFromWishlist(Integer.valueOf(user.getId().toString()), bookId);
            ra.addFlashAttribute("successMessage", "Book removed from wishlist.");
        }
        return "redirect:/wishlist";
    }
}