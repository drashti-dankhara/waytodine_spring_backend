package com.waytodine.waytodine.service;

import com.waytodine.waytodine.model.Category;
import com.waytodine.waytodine.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Get active categories
    public List<Category> listActiveCategories() {
        return categoryRepository.findByStatus(1);
    }


}
