package com.bookstore.online_bookstore.payment;

public class CashPayment implements Payment{

    @Override
    public String processPayment(Double amount){
        System.out.println("Processing CASH payment of Rs. " + amount);
        return "CASH";
    }
}
