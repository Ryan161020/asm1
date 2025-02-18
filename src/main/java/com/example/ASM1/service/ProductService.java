package com.example.ASM1.service;

import com.example.ASM1.Entity.Product;
import com.example.ASM1.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAll() {
return productRepository.findAll();
    }

    public Product save(Product product) {
return productRepository.save(product);
    }


    public Optional<Product> findById(int productId) {
        return productRepository.findById(productId);
    }


}
