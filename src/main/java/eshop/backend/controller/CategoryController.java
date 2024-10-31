package eshop.backend.controller;

import eshop.backend.exception.CategoryNotFoundException;
import eshop.backend.exception.DiscountNotFoundException;
import eshop.backend.exception.InfiniteLoopException;
import eshop.backend.model.Category;
import eshop.backend.request.CategoryRequest;
import eshop.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        var categoryRequestList = categoryService.list();
        return ResponseEntity.ok(categoryRequestList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) throws CategoryNotFoundException {
        var category = categoryService.read(id);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody CategoryRequest categoryRequest) throws CategoryNotFoundException, InfiniteLoopException, DiscountNotFoundException {
        var category = categoryService.create(categoryRequest);
        return ResponseEntity.ok(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody CategoryRequest categoryRequest) throws CategoryNotFoundException, InfiniteLoopException, DiscountNotFoundException {
        if (!Objects.equals(id, categoryRequest.id())) {
            throw new CategoryNotFoundException(id);
        }
        var category = categoryService.update(categoryRequest);
        return ResponseEntity.ok(category);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) throws CategoryNotFoundException {
        categoryService.delete(id);
        return ResponseEntity.ok().build();
    }
}