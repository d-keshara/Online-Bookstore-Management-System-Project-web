package com.bookstore.online_bookstore.service;

import com.bookstore.online_bookstore.entity.PromoCode;
import com.bookstore.online_bookstore.repository.PromoCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PromoCodeService {

    @Autowired
    private PromoCodeRepository promoCodeRepository;

    // 1. Get all promos for the Admin list
    public List<PromoCode> getAllPromos() {
        return promoCodeRepository.findAll();
    }

    // 2. Create or Update a promo
    public void savePromo(PromoCode promoCode) {
        promoCodeRepository.save(promoCode);
    }

    // 3. Delete a promo
    public void deletePromo(Long id) {
        promoCodeRepository.deleteById(id);
    }

    // 4. THE BRAIN: Validate a code when a user enters it in the cart
    public Optional<PromoCode> validateCode(String code) {
        Optional<PromoCode> promoOpt = promoCodeRepository.findByCodeIgnoreCase(code);

        if (promoOpt.isPresent()) {
            PromoCode promo = promoOpt.get();

            // Check if it's currently active and not expired
            boolean isStarted = promo.getStartDate().isBefore(LocalDateTime.now());
            boolean isNotExpired = promo.getEndDate().isAfter(LocalDateTime.now());

            if (isStarted && isNotExpired && promo.isActive()) {
                return Optional.of(promo);
            }
        }
        return Optional.empty(); // Returns empty if code is fake or expired
    }
}