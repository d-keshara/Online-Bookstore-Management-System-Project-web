package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.repository.FeaturedAuthorRepository;
import com.bookstore.online_bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicHomeController {

    @Autowired
    private FeaturedAuthorRepository featuredAuthorRepository;

    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/home")
    public String showLandingPage(Model model) {

        model.addAttribute("vipAuthors", featuredAuthorRepository.findByFeaturedOnHomePageTrue());

        model.addAttribute("topBooks", bookRepository.findAll().stream().limit(4).toList());

        return "index";
    }
}