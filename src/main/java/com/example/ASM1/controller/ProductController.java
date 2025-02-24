package com.example.ASM1.controller;


import com.example.ASM1.Entity.Product;
import com.example.ASM1.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.sql.SQLOutput;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Controller
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;


    @GetMapping("/admin")
    public String admin(Model model) {
        return "admin";
    }


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
    public String save(Product product, @RequestParam("imageFile") MultipartFile file) {
        try {
            if (!file.isEmpty()) {
                // Lấy MIME type của file (ví dụ: image/png, image/jpeg, image/gif,...)
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    // Nếu không xác định được hoặc không phải là ảnh, có thể dùng mặc định
                    contentType = "image/png";
                }
                String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
                // Tạo Data URL động dựa trên MIME type của file
                product.setImageUrl("data:" + contentType + ";base64," + base64Image);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }


        // Kiểm tra giá sản phẩm hợp lệ
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return "redirect:/product/quanLysp";
        }

        // Lưu sản phẩm vào database
        productService.save(product);
        System.out.println("Product saved successfully: " + product);

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

    // Cập nhật sản phẩm
    @PostMapping("/update")
    public String update(Product product, @RequestParam("imageFile") MultipartFile file) {
        try {
            if (!file.isEmpty()) {
                String uploadDir = "C:/uploads/images/";
                java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir);
                if (!java.nio.file.Files.exists(uploadPath)) {
                    java.nio.file.Files.createDirectories(uploadPath);
                }
                String originalFilename = file.getOriginalFilename();
                String fileName = System.currentTimeMillis() + "_" + originalFilename;
                java.nio.file.Path filePath = uploadPath.resolve(fileName);
                file.transferTo(filePath.toFile());
                product.setImageUrl(fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return "redirect:/product/quanLysp";
        }
        productService.save(product);
        System.out.println("Product updated successfully: " + product);
        return "redirect:/product/quanLysp";
    }

    // Phương thức xóa sản phẩm
    @GetMapping("/delete")
    public String delete(@RequestParam("productId") int productId) {
        productService.deleteById(productId);
        return "redirect:/product/quanLysp";
    }






}
