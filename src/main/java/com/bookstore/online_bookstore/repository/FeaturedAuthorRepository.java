package com.bookstore.online_bookstore.repository;

import com.bookstore.online_bookstore.entity.FeaturedAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeaturedAuthorRepository extends JpaRepository<FeaturedAuthor, Long> {

    List<FeaturedAuthor> findByFeaturedOnHomePageTrue();
}