package com.example.ASM1.controller;


import com.example.ASM1.Entity.Product;
import com.example.ASM1.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.SQLOutput;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Controller
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;


    @GetMapping("/findAll")
public String findAll(Model model) {
        List<Product> products = productService.findAll();

        NumberFormat df = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        for (Product product : products) {
            String formattedPrice = df.format(product.getPrice());
            product.setFormattedPrice(formattedPrice); // Gán giá đã định dạng vào thuộc tính mới
        }
        model.addAttribute("products", products);
        return "Index";

    }
    @GetMapping("/quanLysp")
    public String formProduct(Model model) {
        List<Product> products = productService.findAll();
        NumberFormat df = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        for (Product product : products) {
            String formattedPrice = df.format(product.getPrice());
            product.setFormattedPrice(formattedPrice); // Gán giá đã định dạng vào thuộc tính mới
        }
        model.addAttribute("products", products);
        model.addAttribute("product", new Product());
        return "quanLysp";
    }

    @PostMapping("/save")
    public String save(Product product) {
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO)<= 0)
        {
            return "redirect:/product/quanLysp";
        }
        productService.save(product);
        System.out.println("ok");
        return "redirect:/product/quanLysp";
    }




}
