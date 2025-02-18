package com.example.ASM1.repository;

import com.example.ASM1.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findById(int id);
    Category findByName(String name);
    Category findByDescription(String description);
}
