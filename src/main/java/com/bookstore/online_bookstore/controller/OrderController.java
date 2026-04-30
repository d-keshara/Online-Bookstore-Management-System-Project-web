package com.bookstore.online_bookstore.controller;

import java.io.File;
import java.util.List;
import com.bookstore.online_bookstore.entity.Order;
import com.bookstore.online_bookstore.service.OrderService;
import com.bookstore.online_bookstore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/receipt/{orderId}")
    public ResponseEntity<Resource> downloadReceipt(@PathVariable Integer orderId) {
        File file = new File("files/receipt_order_" + orderId + ".txt");
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
    }

    @PostMapping("/checkout")
    public Order createRealOrder(@RequestBody Order newOrderFromFrontend){
        return orderService.placeOrder(newOrderFromFrontend);
    }
}
