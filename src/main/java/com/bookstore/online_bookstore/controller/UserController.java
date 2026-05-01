package com.bookstore.online_bookstore.controller;

import com.bookstore.online_bookstore.entity.User;
import com.bookstore.online_bookstore.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.bookstore.online_bookstore.dto.UserDTO;
import com.bookstore.online_bookstore.entity.Order;
import com.bookstore.online_bookstore.service.OrderService;
import com.bookstore.online_bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(Principal principal) {
        return ResponseEntity.ok(userService.getUserProfile(principal.getName()));
    }

    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestBody UserDTO userDTO, Principal principal) {
        userService.updateUserProfile(principal.getName(), userDTO);
        return ResponseEntity.ok("Profile updated successfully!");
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<Order>> getMyOrders(Principal principal) {
        return ResponseEntity.ok(orderService.getOrdersByUsername(principal.getName()));
    }

    @PutMapping("/{id}/settings")
    public ResponseEntity<?> updateSettingsById(@PathVariable Integer id, @RequestBody Map<String, String> requestData) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        return processUpdate(user, requestData);
    }

    @PutMapping("/settings")
    public ResponseEntity<?> updateUserSettings(@RequestBody Map<String, String> requestData, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return processUpdate(user, requestData);
    }

    private ResponseEntity<?> processUpdate(User user, Map<String, String> requestData) {
        boolean updated = false;

        if (requestData.containsKey("email")) {
            user.setEmail(requestData.get("email"));
            updated = true;
        }

        if (requestData.containsKey("password")) {
            String rawPassword = requestData.get("password");
            if (rawPassword != null && !rawPassword.trim().isEmpty()) {
                user.setPassword(passwordEncoder.encode(rawPassword));
                updated = true;
            }
        }

        if (updated) {
            userRepository.save(user);
            return ResponseEntity.ok("Profile settings successfully updated!");
        } else {
            return ResponseEntity.badRequest().body("No valid fields provided to update.");
        }
    }
}