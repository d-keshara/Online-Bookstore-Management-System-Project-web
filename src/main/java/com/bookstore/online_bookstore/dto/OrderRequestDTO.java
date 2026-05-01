package com.bookstore.online_bookstore.dto;

import java.util.List;

public class OrderRequestDTO {
    private Long userId;
    private List<CartItemDTO> items;

    // Inner class for the items in the cart
    public static class CartItemDTO {
        private Integer bookId;
        private Integer quantity;

        // Getters and Setters
        public Integer getBookId() { return bookId; }
        public void setBookId(Integer bookId) { this.bookId = bookId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public List<CartItemDTO> getItems() { return items; }
    public void setItems(List<CartItemDTO> items) { this.items = items; }
}