package com.ecommerce.category;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        log.info("Fetching category by ID: {}", id);
        CategoryResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/batch")
    public ResponseEntity<List<CategoryOverviewResponse>> getCategoriesByIds(@RequestParam Set<Long> ids) {
        log.info("Fetching categories by IDs: {}", ids);
        List<CategoryOverviewResponse> response = categoryService.getCategoriesByIds(ids);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoryOverviewResponse>> getAllCategories() {
        log.info("Fetching all categories");
        List<CategoryOverviewResponse> response = categoryService.getAllCategories();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Long> createCategory(@RequestBody @Valid CategoryRequest request) {
        log.info("Creating new category: {}", request);
        Long response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Long> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CategoryRequest request
    ) {
        log.info("Updating category with ID: {}", id);
        Long response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable Long id) {
        log.info("Deleting category with ID: {}", id);
        categoryService.deleteCategoryById(id);
        return ResponseEntity.noContent().build();
    }
}
