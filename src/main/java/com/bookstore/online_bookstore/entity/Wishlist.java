package com.bookstore.online_bookstore.entity;

import jakarta.persistence.*;

    @Entity
    @Table(name = "wishlists")
    public class Wishlist {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        @ManyToOne
        @JoinColumn(name = "book_id", nullable = false)
        private Book book;

        // Constructors
        public Wishlist() {}

        public Wishlist(User user, Book book) {
            this.user = user;
            this.book = book;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Book getBook() {
            return book;
        }

        public void setBook(Book book) {
            this.book = book;
        }

}
