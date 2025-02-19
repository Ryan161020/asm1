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
    @GetMapping("/showprd")
    public String showsp(Model model) {
        List<Product> products = productService.findAll();

        NumberFormat df = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        for (Product product : products) {
            String formattedPrice = df.format(product.getPrice());
            product.setFormattedPrice(formattedPrice); // Gán giá đã định dạng vào thuộc tính mới
        }
        model.addAttribute("products", products);
        return "product";

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

    // Phương thức hiển thị form chỉnh sửa sản phẩm
    @GetMapping("/edit")
    public String edit(@RequestParam("id") int productId, Model model) {
        Product product = productService.findById(productId).orElse(null);
        if (product != null) {
            model.addAttribute("product", product);
            return "editProduct";
        }
        return "redirect:/product/quanLysp";
    }


    // Xử lý cập nhật sản phẩm sau khi sửa
    @PostMapping("/update")
    public String update(Product product) {
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return "redirect:/product/quanLysp";
        }
        productService.save(product);
        return "redirect:/product/quanLysp";
    }
    // Phương thức xóa sản phẩm
    @GetMapping("/delete")
    public String delete(@RequestParam("productId") int productId) {
        productService.deleteById(productId);
        return "redirect:/product/quanLysp";
    }






}
