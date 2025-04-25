package com.ecommerce.category;

import com.ecommerce.exception.CategoryNotFoundException;
import com.ecommerce.exception.InfiniteLoopException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryValidatorTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryValidator categoryValidator;

    private Category category;
    private Category parentCategory;
    private Category grandparentCategory;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(3L)
                .name("Test Category")
                .description("Test Description")
                .build();

        parentCategory = Category.builder()
                .id(2L)
                .name("Parent Category")
                .description("Parent Category Description")
                .build();

        grandparentCategory = Category.builder()
                .id(1L)
                .name("Grandparent Category")
                .description("Grandparent Category Description")
                .build();
    }

    @Test
    void validateNoInfiniteLoop_NullParent_NoException() {
        categoryValidator.validateNoInfiniteLoop(category, null);
    }

    @Test
    void validateNoInfiniteLoop_ValidHierarchy_NoException() {
        parentCategory.setParent(grandparentCategory);
        grandparentCategory.setParent(null);

        categoryValidator.validateNoInfiniteLoop(category, parentCategory);
    }

    @Test
    void validateNoInfiniteLoop_SelfParent_ThrowsInfiniteLoopException() {
        assertThrows(InfiniteLoopException.class, () ->
                categoryValidator.validateNoInfiniteLoop(category, category));
    }

    @Test
    void validateNoInfiniteLoop_CyclicReference_ThrowsInfiniteLoopException() {
        Category categoryA = Category.builder().id(1L).name("Category A").build();
        Category categoryB = Category.builder().id(2L).name("Category B").build();
        Category categoryC = Category.builder().id(3L).name("Category C").build();

        categoryB.setParent(categoryA);
        categoryC.setParent(categoryB);
        categoryA.setParent(categoryC);

        assertThrows(InfiniteLoopException.class, () ->
                categoryValidator.validateNoInfiniteLoop(categoryA, categoryC));
    }

    @Test
    void validateCategoriesExist_EmptySet_NoException() {
        Set<Long> emptySet = Collections.emptySet();

        categoryValidator.validateCategoriesExist(emptySet);

        verify(categoryRepository, never()).countCategoriesByIds(emptySet);
    }

    @Test
    void validateCategoriesExist_AllCategoriesExist_NoException() {
        Set<Long> categoryIds = Set.of(1L, 2L, 3L);
        when(categoryRepository.countCategoriesByIds(categoryIds)).thenReturn(3);

        categoryValidator.validateCategoriesExist(categoryIds);

        verify(categoryRepository, times(1)).countCategoriesByIds(categoryIds);
    }

    @Test
    void validateCategoriesExist_MissingCategory_ThrowsCategoryNotFoundException() {
        Set<Long> categoryIds = Set.of(1L, 2L, 3L);
        when(categoryRepository.countCategoriesByIds(categoryIds)).thenReturn(2);

        assertThrows(CategoryNotFoundException.class, () ->
                categoryValidator.validateCategoriesExist(categoryIds));
        verify(categoryRepository, times(1)).countCategoriesByIds(categoryIds);
    }

    @Test
    void validateCategoriesExist_AllCategoriesMissing_ThrowsCategoryNotFoundException() {
        Set<Long> categoryIds = Set.of(99L, 100L);
        when(categoryRepository.countCategoriesByIds(categoryIds)).thenReturn(0);

        assertThrows(CategoryNotFoundException.class, () ->
                categoryValidator.validateCategoriesExist(categoryIds));
        verify(categoryRepository, times(1)).countCategoriesByIds(categoryIds);
    }
}