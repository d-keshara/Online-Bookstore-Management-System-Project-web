package com.bookstore.online_bookstore.repository;

import com.bookstore.online_bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface UserRepository extends JpaRepository<User, Integer> {

    long countByRole(String role);

    Optional<User> findByUsername(String username);

    User findUserByUsername(String username);
}