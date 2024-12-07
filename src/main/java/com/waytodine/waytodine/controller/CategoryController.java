package com.waytodine.waytodine.controller;

import com.waytodine.waytodine.model.Category;
import com.waytodine.waytodine.service.CategoryService;
import com.waytodine.waytodine.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // List active categories
    @GetMapping("/get-all-categories")
    public ResponseEntity<ApiResponse> getActiveCategories() {
        List<Category> activeCategories = categoryService.listActiveCategories();

        if (activeCategories.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("No categories found.", null, false));
        }

        return ResponseEntity.ok(new ApiResponse("categories fetched successfully", activeCategories, true));
    }


}
