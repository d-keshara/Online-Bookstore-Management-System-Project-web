package com.bookstore.online_bookstore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "featured_authors")

public class FeaturedAuthor extends Author {

    @Column(name = "award_title")
    private String awardTitle;

    @Column(name = "featured_on_home_page", nullable = false)
    private boolean featuredOnHomePage = false;

    public String getAwardTitle() { return awardTitle; }
    public void setAwardTitle(String awardTitle) { this.awardTitle = awardTitle; }
    public boolean isFeaturedOnHomePage() { return featuredOnHomePage; }
    public void setFeaturedOnHomePage(boolean featuredOnHomePage) { this.featuredOnHomePage = featuredOnHomePage; }
}