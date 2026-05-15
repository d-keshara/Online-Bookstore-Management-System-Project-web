package com.bookstore.online_bookstore.service;

import com.bookstore.online_bookstore.entity.Author;
import com.bookstore.online_bookstore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuthorService {

    Author createAuthor(Author author);

    Page<Author> getAllAuthors(Pageable pageable);

    Author getAuthorById(Long id);

    Author updateAuthor(Long id, Author updatedAuthor);

    void deleteAuthor(Long id);

    Page<Book> getBooksByAuthorId(Long authorId, Pageable pageable);

    List<Author> getVipAuthors();
    List<Author> getFeaturedAuthors();
}