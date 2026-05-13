package com.bookstore.online_bookstore.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "promo_codes")
public class PromoCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private double discountPercentage;
    private LocalDateTime startDate; // Ensure this is exactly 'startDate'
    private LocalDateTime endDate;   // Ensure this is exactly 'endDate'
    private boolean isActive;

    // 🔥 HE NEEDS THESE GETTERS 🔥
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    // (Also add getters/setters for id, code, and discountPercentage!)
}