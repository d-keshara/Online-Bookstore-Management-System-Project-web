package com.bookstore.online_bookstore.controller;

import java.util.List;
import com.bookstore.online_bookstore.entity.Order;
import com.bookstore.online_bookstore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController              //handle web requests
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/")         //listen to localhost
    public String welcome(){
        return "Welcome to the Bookstore Order Management System! The engine is running.";

    }
    @GetMapping("/test-save")
    public String testSaveOrder(){
//create a empty object
        Order newOrder = new Order();
//add data
        newOrder.setTotalPrice(1500.50);
//tell repository to save it to MySQL
        orderRepository.save(newOrder);

        return "Success! A new order for 1500.50 was saved to the database.";
    }

    @GetMapping("/all-orders")
    public List<Order> viewAllOrders(){
        return orderRepository.findAll();
    }

    @PostMapping("/create-order")
    public Order createRealOrder(@RequestBody Order newOrderFromFrontend){
        return orderRepository.save(newOrderFromFrontend);
    }
}
