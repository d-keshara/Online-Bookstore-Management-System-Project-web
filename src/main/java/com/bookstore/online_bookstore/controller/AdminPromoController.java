package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.PromoCode;
import com.bookstore.online_bookstore.service.PromoCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/promos")
public class AdminPromoController {

    @Autowired
    private PromoCodeService promoService;

    // READ - List all promo codes
    @GetMapping("/list")
    public String listPromos(Model model) {
        model.addAttribute("promos", promoService.getAllPromos());
        return "promo-list";
    }

    // CREATE - Show form
    @GetMapping("/add")
    public String showPromoForm(Model model) {
        model.addAttribute("promoCode", new PromoCode());
        return "promo-form";
    }

    // CREATE/UPDATE - Save promo
    @PostMapping("/save")
    public String savePromo(@ModelAttribute PromoCode promoCode, RedirectAttributes ra) {
        promoService.savePromo(promoCode);
        ra.addFlashAttribute("successMessage", "Promo Code activated successfully!");
        return "redirect:/admin/promos/list";
    }

    // DELETE - Remove promo
    @PostMapping("/delete/{id}")
    public String deletePromo(@PathVariable Long id, RedirectAttributes ra) {
        promoService.deletePromo(id);
        ra.addFlashAttribute("successMessage", "Promo Code deactivated and removed.");
        return "redirect:/admin/promos/list";
    }
}
