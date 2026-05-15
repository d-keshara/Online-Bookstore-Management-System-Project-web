package com.bookstore.online_bookstore.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String biography;

    @Column
    private String awardTitle;

    @Column(name = "featured_on_home_page")
    private boolean featuredOnHomePage;

    private String profileImageUrl;

    // NEW: The "Glue" linking the profile to a user account
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("author")
    private List<Book> books = new ArrayList<>();

    // Standard Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public List<Book> getBooks() { return books; }
    public void setBooks(List<Book> books) { this.books = books; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getAwardTitle() { return awardTitle; }
    public void setAwardTitle(String awardTitle) { this.awardTitle = awardTitle; }

    public boolean isFeaturedOnHomePage() { return featuredOnHomePage; }
    public void setFeaturedOnHomePage(boolean featuredOnHomePage) { this.featuredOnHomePage = featuredOnHomePage; }
}