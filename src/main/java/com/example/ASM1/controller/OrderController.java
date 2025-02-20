package com.example.ASM1.controller;

import com.example.ASM1.Entity.Order;
import com.example.ASM1.Entity.User;
import com.example.ASM1.service.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderServiceImpl orderService;

    // Phương thức GET để hiển thị trang quanLyUser với form nhập người dùng
//    @GetMapping("/quanlyOder")
//    public String quanLyOder(Model model) {
//        List<Order> orders = orderService.findAllOrders();
//        model.addAttribute("orders", orders);
//        model.addAttribute("order", new Order());  // Chỉ cần thêm 1 lần
//        return "quanLyUser";
//    }

    @GetMapping("/findAll")
    public String findAll(Model model) {
        List<Order> orders = orderService.findAllOrders();
        model.addAttribute("orders", orders);
        model.addAttribute("order", new Order());
        return "quanLyDonHang";

    }
}
