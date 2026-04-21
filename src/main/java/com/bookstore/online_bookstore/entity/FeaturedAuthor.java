package com.bookstore.online_bookstore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "featured_authors")
public class FeaturedAuthor extends Author {

    private String awardTitle;
    private boolean featuredOnHomePage;
}