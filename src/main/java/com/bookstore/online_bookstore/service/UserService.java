package com.bookstore.online_bookstore.service;

import com.bookstore.online_bookstore.dto.UserDTO;
import com.bookstore.online_bookstore.entity.User;
import com.bookstore.online_bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserDTO getUserProfile(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        return new UserDTO(
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getAddress(),
                user.getPhoneNumber()
        );
    }

    public void updateUserProfile(String username, UserDTO userDTO) {
        //Find the existing user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        //Update the fields with data from the DTO
        user.setFullName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail());
        user.setAddress(userDTO.getAddress());
        user.setPhoneNumber(userDTO.getPhoneNumber());

        //Save the updated entity back to the database
        userRepository.save(user);
    }
}
