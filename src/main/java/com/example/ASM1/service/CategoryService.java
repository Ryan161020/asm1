


package com.example.ASM1.service;

import com.example.ASM1.Entity.Category;
import com.example.ASM1.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category getCategoryById(int categoryId) {
        return categoryRepository.findById(categoryId);
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}

