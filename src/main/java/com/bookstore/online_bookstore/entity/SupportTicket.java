package com.bookstore.online_bookstore.entity;


import jakarta.persistence.*;

    @Entity
    @Table(name = "support_tickets")
    public class SupportTicket {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String subject;

        @Column(nullable = false, length = 1000)
        private String message;

        @Column(nullable = false)
        private String status = "OPEN"; // OPEN, IN_PROGRESS, or RESOLVED

        // Connects the ticket to the user who submitted it!
        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        // --- GETTERS AND SETTERS (Generated for Hamsana) ---

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }

