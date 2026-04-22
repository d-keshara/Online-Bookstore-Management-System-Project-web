package com.bookstore.online_bookstore.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@SuppressWarnings("JpaDataSourceORMInspection")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true , nullable = false)
    private String username;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String role;

    //New Getters and Setters For profile management
    //New Fields for profile management
    private String fullName;

    @Column(name = "email")
    private String email;

    private String address;
    private String phoneNumber;

    public User(){}

}


