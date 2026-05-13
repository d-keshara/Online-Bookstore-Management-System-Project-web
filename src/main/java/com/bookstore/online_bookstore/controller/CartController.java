package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.Book;
import com.bookstore.online_bookstore.entity.CartItem;
import com.bookstore.online_bookstore.entity.PromoCode;
import com.bookstore.online_bookstore.entity.User;
import com.bookstore.online_bookstore.repository.BookRepository;
import com.bookstore.online_bookstore.repository.CartItemRepository;
import com.bookstore.online_bookstore.repository.UserRepository;
import com.bookstore.online_bookstore.service.CartService;
import com.bookstore.online_bookstore.service.PromoCodeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final PromoCodeService promoCodeService;

    public CartController(CartItemRepository cartItemRepository, BookRepository bookRepository,
                          UserRepository userRepository, CartService cartService, PromoCodeService promoCodeService) {
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.promoCodeService = promoCodeService;
    }

    @GetMapping
    public String viewCart(Model model, Principal principal, HttpSession session) {
        if (principal == null) return "redirect:/login";
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user == null) return "redirect:/login";

        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        BigDecimal subTotal = BigDecimal.valueOf(cartService.calculateTotal(user));
        BigDecimal finalTotal = subTotal;
        BigDecimal discountAmount = BigDecimal.ZERO;

        // Check if a discount is in the session and apply it
        Double discountPercentage = (Double) session.getAttribute("appliedDiscount");
        if (discountPercentage != null && subTotal.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal multiplier = BigDecimal.valueOf(discountPercentage / 100.0);
            discountAmount = subTotal.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
            finalTotal = subTotal.subtract(discountAmount);

            model.addAttribute("discountPercentage", discountPercentage);
            model.addAttribute("discountAmount", discountAmount);
            model.addAttribute("appliedPromoCode", session.getAttribute("appliedPromoCode"));
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subTotal", subTotal);
        model.addAttribute("cartTotal", finalTotal);
        return "cart";
    }

    // THE VALIDATION ENGINE
    @PostMapping("/apply-promo")
    public String applyPromo(@RequestParam("code") String code, HttpSession session, RedirectAttributes ra) {
        Optional<PromoCode> validPromo = promoCodeService.validateCode(code);

        if (validPromo.isPresent()) {
            PromoCode promo = validPromo.get();
            session.setAttribute("appliedDiscount", promo.getDiscountPercentage());
            session.setAttribute("appliedPromoCode", promo.getCode());
            ra.addFlashAttribute("promoSuccess", "Success! " + promo.getDiscountPercentage() + "% discount applied.");
        } else {
            ra.addFlashAttribute("promoError", "Invalid or expired promo code.");
        }

        return "redirect:/cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("bookId") Integer bookId,
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                            Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        Book book = bookRepository.findById(bookId).orElse(null);

        if (user != null && book != null) {
            cartService.addToCart(user, book, quantity);
        }

        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam("itemId") Integer itemId, @RequestParam("action") String action) {
        CartItem item = cartItemRepository.findById(itemId).orElse(null);
        if (item != null) {
            if ("increase".equals(action)) {
                item.setQuantity(item.getQuantity() + 1);
                cartItemRepository.save(item);
            } else if ("decrease".equals(action)) {
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                    cartItemRepository.save(item);
                } else {
                    cartItemRepository.delete(item);
                }
            }
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam("itemId") Integer itemId) {
        cartItemRepository.deleteById(itemId);
        return "redirect:/cart";
    }

    @Transactional
    @PostMapping("/checkout")
    public String checkoutCart(Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user == null) return "redirect:/login";

        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) return "redirect:/cart?empty";

        return "redirect:/orders/checkout-payment";
    }
}