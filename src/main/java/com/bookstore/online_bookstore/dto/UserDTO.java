package com.bookstore.online_bookstore.dto;

public class UserDTO {
    private String username;
    private String fullName;
    private String email;
    private String address;
    private String phoneNumber;

    public UserDTO(){}

    public UserDTO(String username, String fullName, String email, String address, String phoneNumber) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
