package com.bookstore.online_bookstore.controller;

import java.util.List;
import com.bookstore.online_bookstore.entity.Order;
import com.bookstore.online_bookstore.service.OrderService;
import com.bookstore.online_bookstore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;


    @GetMapping("/")         //listen to localhost
    public String welcome(){
        return "Welcome to the Bookstore Order Management System! The engine is running.";
    }

    @GetMapping("/test-save")
    public String testSaveOrder(){
        Order newOrder = new Order();
        newOrder.setTotalPrice(BigDecimal.valueOf(1500.50));
        orderRepository.save(newOrder);
        return "Success! A new order for 1500.50 was saved to the database.";
    }

    @GetMapping("/all-orders")
    public List<Order> viewAllOrders(){
        return orderRepository.findAll();
    }

    @PostMapping("/checkout")
    public Order createRealOrder(@RequestBody Order newOrderFromFrontend){
        return orderService.placeOrder(newOrderFromFrontend);
    }
}