package com.bookstore.online_bookstore.controller;

import java.util.List;
import com.bookstore.online_bookstore.entity.Order;
import com.bookstore.online_bookstore.service.OrderService; // <-- This tells the controller where your service is
import com.bookstore.online_bookstore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController              //handle web requests
public class OrderController {

    // 1. We bring in your new Service layer
    @Autowired
    private OrderService orderService;

    // 2. We keep the repository ONLY for the quick tests you wrote earlier
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/")         //listen to localhost
    public String welcome(){
        return "Welcome to the Bookstore Order Management System! The engine is running.";
    }

    @GetMapping("/test-save")
    public String testSaveOrder(){
        Order newOrder = new Order();
        newOrder.setTotalPrice(1500.50);
        orderRepository.save(newOrder);
        return "Success! A new order for 1500.50 was saved to the database.";
    }

    @GetMapping("/all-orders")
    public List<Order> viewAllOrders(){
        return orderRepository.findAll();
    }

    // 3. THIS is where we use your new method!
    @PostMapping("/create-order")
    public Order createRealOrder(@RequestBody Order newOrderFromFrontend){
        // We call the placeOrder method that lives inside the OrderService class
        return orderService.placeOrder(newOrderFromFrontend);
    }
}