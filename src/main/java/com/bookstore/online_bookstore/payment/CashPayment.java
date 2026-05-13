package com.bookstore.online_bookstore.payment;

public class CashPayment implements Payment {

    @Override
    public String processPayment(Double amount) {
        // Simulated Enterprise Logistics Logging
        System.out.println("=================================================");
        System.out.println("[NEXUS LOGISTICS] Order flagged for Cash on Delivery.");
        System.out.println("[NEXUS LOGISTICS] Dispatching rider.");
        System.out.println("[NEXUS LOGISTICS] Expected cash collection: LKR " + amount + " at doorstep.");
        System.out.println("=================================================");

        return "CASH";
    }
}