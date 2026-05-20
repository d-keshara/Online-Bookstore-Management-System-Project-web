package com.bookstore.online_bookstore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "featured_authors")
public class FeaturedAuthor extends Author {

    // Changed primitive 'boolean' to object 'Boolean'
    @Column(name = "featured_on_home_page", nullable = false)
    private Boolean featuredOnHomePage = false;

    // Updated to use 'get' instead of 'is' for the Boolean object
    public Boolean getFeaturedOnHomePage() { return featuredOnHomePage; }
    public void setFeaturedOnHomePage(Boolean featuredOnHomePage) { this.featuredOnHomePage = featuredOnHomePage; }
}