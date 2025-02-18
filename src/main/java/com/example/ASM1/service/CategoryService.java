package com.example.ASM1.service;

import com.example.ASM1.Entity.Category;
import com.example.ASM1.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    public Category getCategoryById(int id) {
        // ví dụ
        // muốn thay đổi tên của Cat này thì
//        Category category = categoryRepository.findById(id);
//        category.setName("vâng vâng mây mây");
        // còn nếu ko muốn sửa gì thì return thẳng
        return categoryRepository.findById(id);
    }
}
