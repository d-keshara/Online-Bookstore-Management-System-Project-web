package com.bookstore.online_bookstore.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "promo_codes")
public class PromoCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private Double discountPercentage;

    @Column(nullable = false)
    private LocalDate expiryDate;

    private boolean isActive = true;

    // TODO: Akindu, generate Getters and Setters here!
}