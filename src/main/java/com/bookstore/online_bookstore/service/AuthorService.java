package com.bookstore.online_bookstore.service;

import com.bookstore.online_bookstore.entity.Author;
import com.bookstore.online_bookstore.entity.Book;

import java.util.List;

public interface AuthorService {
    Author createAuthor(Author author);
    List<Author> getAllAuthors();
    Author getAuthorById(Long id);
    Author updateAuthor(Long id, Author updatedAuthor);
    void deleteAuthor(Long id);
    List<Book> getBooksByAuthorId(Long authorId);
}