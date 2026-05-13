package com.bookstore.online_bookstore.repository;

import com.bookstore.online_bookstore.entity.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    // This allows the service to find the code regardless of Caps Lock
    Optional<PromoCode> findByCodeIgnoreCase(String code);
}