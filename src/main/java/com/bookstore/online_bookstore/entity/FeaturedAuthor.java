package com.bookstore.online_bookstore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "featured_authors")
public class FeaturedAuthor extends Author {

    private String awardTitle;
    private boolean featuredOnHomePage;

    public String getAwardTitle() {
        return awardTitle;
    }

    public void setAwardTitle(String awardTitle) {
        this.awardTitle = awardTitle;
    }

    public boolean isFeaturedOnHomePage() {
        return featuredOnHomePage;
    }

    public void setFeaturedOnHomePage(boolean featuredOnHomePage) {
        this.featuredOnHomePage = featuredOnHomePage;
    }
}
