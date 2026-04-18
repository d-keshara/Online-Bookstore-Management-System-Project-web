package com.bookstore.online_bookstore.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Integer bookId;

    @Column(name = "title")
    private String title;

    @Column(name = "price")
    private Double price;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "category")
    private String category;

}
























