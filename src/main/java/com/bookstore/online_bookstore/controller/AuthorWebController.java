package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.Author;
import com.bookstore.online_bookstore.service.AuthorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/authors")
public class AuthorWebController {

    private final AuthorService authorService;

    public AuthorWebController(AuthorService authorService) {
        this.authorService = authorService;
    }

    // 1. View all authors (Paginated)
    @GetMapping
    public String listAuthors(Model model, @RequestParam(defaultValue = "0") int page) {
        // Fetch 10 authors per page using her existing service
        Pageable pageable = PageRequest.of(page, 10);
        Page<Author> authorPage = authorService.getAllAuthors(pageable);

        model.addAttribute("authors", authorPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", authorPage.getTotalPages());

        return "authors-list";
    }

    // 2. Show the "Add New Author" form
    @GetMapping("/add")
    public String showAddAuthorForm(Model model) {
        model.addAttribute("author", new Author());
        return "add-author";
    }

    // 3. Save the author from the HTML form
    @PostMapping("/save")
    public String saveAuthor(@ModelAttribute("author") Author author) {
        authorService.createAuthor(author);
        return "redirect:/admin/authors?success";
    }
}
