package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.User;
import com.bookstore.online_bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //Register a new user
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists!");
        }

        // Hash the password before saving for security
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Force EVERYONE who uses this public endpoint to be a standard user.
        user.setRole("ROLE_USER");

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }


    // 2. Login (For check)
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User loginRequest) {
        return ResponseEntity.ok("Login logic successful. Now use your credentials in Postman Auth tab.");
    }
}