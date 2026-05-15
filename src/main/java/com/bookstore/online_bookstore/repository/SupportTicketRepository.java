package com.bookstore.online_bookstore.repository;

import com.bookstore.online_bookstore.entity.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    // Spring Boot automatically creates the SQL to find, save, and delete tickets!
    long countByStatus(String status);
}

