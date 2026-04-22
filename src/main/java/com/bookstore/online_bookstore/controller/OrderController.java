package com.bookstore.online_bookstore.controller;

import java.util.List;
import com.bookstore.online_bookstore.entity.Order;
import com.bookstore.online_bookstore.service.OrderService;
import com.bookstore.online_bookstore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        return "Use POST /api/orders/checkout with a valid user and items payload to create an order.";
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
