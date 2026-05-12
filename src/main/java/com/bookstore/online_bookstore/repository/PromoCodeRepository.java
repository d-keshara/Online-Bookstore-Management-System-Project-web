package com.bookstore.online_bookstore.repository;

import com.bookstore.online_bookstore.entity.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    PromoCode findByCode(String code); // Custom method for him to find codes easily!
}