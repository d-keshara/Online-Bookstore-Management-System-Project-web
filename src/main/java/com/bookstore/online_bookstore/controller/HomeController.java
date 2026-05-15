package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.repository.AuthorRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final AuthorRepository authorRepository;

    public HomeController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("vipAuthors", authorRepository.findByFeaturedOnHomePageTrue());
        return "index";
    }
}