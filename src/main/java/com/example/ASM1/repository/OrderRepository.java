package com.example.ASM1.repository;

import com.example.ASM1.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Các truy vấn tùy chỉnh nếu cần
}
