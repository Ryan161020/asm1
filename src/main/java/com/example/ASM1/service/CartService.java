package com.example.ASM1.service;

import com.example.ASM1.Entity.CartItem;
import com.example.ASM1.Entity.Order;
import com.example.ASM1.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {
    @Autowired
    private OrderRepository orderRepository;


    public List<CartItem> getCart(HttpSession session) {
        return (List<CartItem>) session.getAttribute("cart");
    }

    public BigDecimal calculateTotal(List<CartItem> cart) {
        return cart.stream().map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void clearCart(HttpSession session) {
        session.removeAttribute("cart");
    }

}
