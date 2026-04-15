package com.bookstore.online_bookstore.payment;

public class CardPayment implements Payment{

    @Override
    public String processPayment(Double amount){
        System.out.println("Processing CREDIT CARD payment of Rs. "+amount+" via Bank Gateway...");
        return "CARD";
    }

}
