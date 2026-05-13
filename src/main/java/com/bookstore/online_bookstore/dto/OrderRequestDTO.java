package com.bookstore.online_bookstore.dto;

import java.util.List;

public class OrderRequestDTO {
    private Integer userId;
    private List<CartItemDTO> items;

    private Double discountPercentage;
    private String promoCode;

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
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public List<CartItemDTO> getItems() { return items; }
    public void setItems(List<CartItemDTO> items) { this.items = items; }

    public Double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(Double discountPercentage) { this.discountPercentage = discountPercentage; }

    public String getPromoCode() { return promoCode; }
    public void setPromoCode(String promoCode) { this.promoCode = promoCode; }
}