package com.ecommerce.category;

import com.ecommerce.exception.CategoryNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private CategoryResponse categoryResponse;
    private CategoryOverviewResponse categoryOverviewResponse;
    private CategoryRequest categoryRequest;

    @BeforeEach
    void setUp() {
        categoryResponse = CategoryResponse.builder()
                .id(1L)
                .name("Test Category")
                .description("Test Description")
                .parent(null)
                .children(Collections.emptyList())
                .build();

        categoryOverviewResponse = CategoryOverviewResponse.builder()
                .id(1L)
                .name("Test Category")
                .description("Test Description")
                .children(Collections.emptyList())
                .build();

        categoryRequest = new CategoryRequest(
                null,
                "Test Category",
                "Test Description",
                null
        );
    }

    @Test
    void getCategoryById_ExistingId_ReturnsOkWithCategory() {
        Long categoryId = 1L;
        when(categoryService.getCategoryById(categoryId)).thenReturn(categoryResponse);

        ResponseEntity<CategoryResponse> response = categoryController.getCategoryById(categoryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(categoryResponse, response.getBody());
        verify(categoryService, times(1)).getCategoryById(categoryId);
    }

    @Test
    void getCategoryById_NonExistingId_ThrowsCategoryNotFoundException() {
        Long nonExistingId = 99L;
        when(categoryService.getCategoryById(nonExistingId))
                .thenThrow(new CategoryNotFoundException("Kategorie s ID 99 nebyla nalezena."));

        assertThrows(CategoryNotFoundException.class, () -> {
            categoryController.getCategoryById(nonExistingId);
        });
        verify(categoryService, times(1)).getCategoryById(nonExistingId);
    }

    @Test
    void getCategoriesByIds_ValidIds_ReturnsOkWithCategories() {
        Set<Long> categoryIds = Set.of(1L, 2L);
        List<CategoryOverviewResponse> overviewResponses = Arrays.asList(
                categoryOverviewResponse,
                CategoryOverviewResponse.builder()
                        .id(2L)
                        .name("Another Category")
                        .description("Another Description")
                        .children(Collections.emptyList())
                        .build()
        );

        when(categoryService.getCategoriesByIds(categoryIds)).thenReturn(overviewResponses);

        ResponseEntity<List<CategoryOverviewResponse>> response = categoryController.getCategoriesByIds(categoryIds);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(overviewResponses, response.getBody());
        verify(categoryService, times(1)).getCategoriesByIds(categoryIds);
    }

    @Test
    void getAllCategories_ReturnsOkWithAllCategories() {
        List<CategoryOverviewResponse> allCategories = Arrays.asList(
                categoryOverviewResponse,
                CategoryOverviewResponse.builder()
                        .id(2L)
                        .name("Another Category")
                        .description("Another Description")
                        .children(Collections.emptyList())
                        .build()
        );

        when(categoryService.getAllCategories()).thenReturn(allCategories);

        ResponseEntity<List<CategoryOverviewResponse>> response = categoryController.getAllCategories();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(allCategories, response.getBody());
        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    void createCategory_ValidRequest_ReturnsCreatedWithCategoryId() {
        Long newCategoryId = 1L;
        when(categoryService.createCategory(categoryRequest)).thenReturn(newCategoryId);

        ResponseEntity<Long> response = categoryController.createCategory(categoryRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newCategoryId, response.getBody());
        verify(categoryService, times(1)).createCategory(categoryRequest);
    }

    @Test
    void updateCategory_ValidRequest_ReturnsOkWithCategoryId() {
        Long categoryId = 1L;
        when(categoryService.updateCategory(categoryId, categoryRequest)).thenReturn(categoryId);

        ResponseEntity<Long> response = categoryController.updateCategory(categoryId, categoryRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(categoryId, response.getBody());
        verify(categoryService, times(1)).updateCategory(categoryId, categoryRequest);
    }

    @Test
    void deleteCategoryById_ExistingId_ReturnsNoContent() {
        Long categoryId = 1L;
        doNothing().when(categoryService).deleteCategoryById(categoryId);

        ResponseEntity<Void> response = categoryController.deleteCategoryById(categoryId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(categoryService, times(1)).deleteCategoryById(categoryId);
    }

}