package com.bookstore.online_bookstore.service;

import com.bookstore.online_bookstore.entity.Author;
import com.bookstore.online_bookstore.entity.Book;
import com.bookstore.online_bookstore.repository.AuthorRepository;
import com.bookstore.online_bookstore.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    // THIS METHOD WAS MISSING!
    @Override
    public Author createAuthor(Author author) {
        return authorRepository.save(author);
    }

    @Override
    public Page<Author> getAllAuthors(Pageable pageable) {
        return authorRepository.findAll(pageable);
    }

    @Override
    public Author getAuthorById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));
    }

    @Override
    public Author updateAuthor(Long id, Author updatedAuthor) {
        Author author = getAuthorById(id);
        author.setName(updatedAuthor.getName());
        author.setBiography(updatedAuthor.getBiography());
        author.setProfileImageUrl(updatedAuthor.getProfileImageUrl());
        author.setAwardTitle(updatedAuthor.getAwardTitle());
        author.setFeaturedOnHomePage(updatedAuthor.isFeaturedOnHomePage());
        return authorRepository.save(author);
    }

    @Override
    public void deleteAuthor(Long id) {
        Author author = getAuthorById(id);
        authorRepository.delete(author);
    }

    @Override
    public Page<Book> getBooksByAuthorId(Long authorId, Pageable pageable) {
        getAuthorById(authorId);
        return bookRepository.findByAuthorId(authorId, pageable);
    }

    @Override
    public List<Author> getVipAuthors() {
        return authorRepository.findByAwardTitleIsNotNull();
    }

    @Override
    public List<Author> getFeaturedAuthors() {
        // Returns all authors — you can later add filtering/sorting here
        return authorRepository.findAll();
    }

}