package com.bookstore.online_bookstore.security;

import com.bookstore.online_bookstore.entity.User;
import com.bookstore.online_bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.findByUsername("superadmin").isEmpty()) {
            System.out.println(">>> Super Admin is missing. Hatching the Admin egg...");

            User firstAdmin = new User();
            firstAdmin.setUsername("superadmin");
            firstAdmin.setPassword(passwordEncoder.encode("admin123"));
            firstAdmin.setRole("ROLE_ADMIN");
            firstAdmin.setEmail("store-owner@gmail.com");

            userRepository.save(firstAdmin);

            System.out.println(">>> First Admin account created automatically! (Username: superadmin)");
        } else {
            System.out.println(">>> Superadmin already exists. Skipping Seeder.");
        }
    }
}