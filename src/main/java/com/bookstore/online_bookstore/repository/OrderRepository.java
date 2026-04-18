package com.bookstore.online_bookstore.repository;

import com.bookstore.online_bookstore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>{

    List<Order> findByUserUsernameOrderByOrderDateDesc(String username);
}
