package com.example.cinema.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.cinema.entity.Order;
import com.example.cinema.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }
}
