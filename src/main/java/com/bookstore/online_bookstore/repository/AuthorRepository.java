package com.bookstore.online_bookstore.repository;

import com.bookstore.online_bookstore.entity.Author;
import com.bookstore.online_bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    Author findByUser(User user);
    List<Author> findByAwardTitleIsNotNull();

    // 🔥 Added the missing method!
    List<Author> findByFeaturedOnHomePageTrue();
}