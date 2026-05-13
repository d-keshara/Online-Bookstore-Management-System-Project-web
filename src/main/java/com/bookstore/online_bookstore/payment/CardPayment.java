package com.bookstore.online_bookstore.payment;

public class CardPayment implements Payment {

    @Override
    public String processPayment(Double amount) {
        // Simulated Enterprise Bank Gateway Logging
        System.out.println("=================================================");
        System.out.println("[NEXUS SECURE GATEWAY] Initiating SSL Handshake...");
        System.out.println("[NEXUS SECURE GATEWAY] Processing CREDIT CARD payment of LKR " + amount);
        System.out.println("[NEXUS SECURE GATEWAY] Verification successful.");
        System.out.println("[NEXUS SECURE GATEWAY] Transaction Approved. Auth Code: TXN-" + (int)(Math.random() * 1000000));
        System.out.println("=================================================");

        return "CARD";
    }
}