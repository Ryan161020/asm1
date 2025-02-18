package com.example.ASM1.service;

import com.example.ASM1.Entity.Order;
import com.example.ASM1.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Override

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }
}
