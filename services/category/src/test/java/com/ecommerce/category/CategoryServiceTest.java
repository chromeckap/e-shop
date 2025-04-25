package com.ecommerce.category;

import com.ecommerce.exception.CategoryNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CategoryValidator categoryValidator;

    @InjectMocks
    private CategoryService categoryService;

    private Category parentCategory;
    private Category childCategory;
    private CategoryRequest categoryRequest;
    private CategoryResponse categoryResponse;
    private CategoryOverviewResponse categoryOverviewResponse;
    private CategoryOverviewResponse parentOverviewResponse;

    @BeforeEach
    void setUp() {
        parentCategory = Category.builder()
                .id(1L)
                .name("Parent Category")
                .description("Parent Category Description")
                .children(Collections.emptyList())
                .build();

        childCategory = Category.builder()
                .id(2L)
                .name("Child Category")
                .description("Child Category Description")
                .parent(parentCategory)
                .children(Collections.emptyList())
                .build();

        categoryRequest = new CategoryRequest(
                null,
                "Test Category",
                "Test Description",
                1L
        );

        parentOverviewResponse = CategoryOverviewResponse.builder()
                .id(1L)
                .name("Parent Category")
                .description("Parent Category Description")
                .children(Collections.emptyList())
                .build();

        categoryOverviewResponse = CategoryOverviewResponse.builder()
                .id(2L)
                .name("Child Category")
                .description("Child Category Description")
                .children(Collections.emptyList())
                .build();

        categoryResponse = CategoryResponse.builder()
                .id(2L)
                .name("Child Category")
                .description("Child Category Description")
                .parent(parentOverviewResponse)
                .children(Collections.emptyList())
                .build();
    }

    @Test
    void findCategoryEntityById_ExistingId_ReturnsCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));

        Category result = categoryService.findCategoryEntityById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void findCategoryEntityById_NonExistingId_ThrowsCategoryNotFoundException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.findCategoryEntityById(99L));
        verify(categoryRepository, times(1)).findById(99L);
    }

    @Test
    void findCategoryEntityById_NullId_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> categoryService.findCategoryEntityById(null));
        verify(categoryRepository, never()).findById(any());
    }

    @Test
    void getCategoryById_ExistingId_ReturnsCategoryResponse() {
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(childCategory));
        when(categoryMapper.toResponse(childCategory)).thenReturn(categoryResponse);

        CategoryResponse result = categoryService.getCategoryById(2L);

        assertNotNull(result);
        assertEquals(2L, result.id());
        assertEquals("Child Category", result.name());
        assertEquals("Child Category Description", result.description());
        assertNotNull(result.parent());
        assertEquals(1L, result.parent().id());
        verify(categoryRepository, times(1)).findById(2L);
        verify(categoryMapper, times(1)).toResponse(childCategory);
    }

    @Test
    void getCategoriesByIds_ValidIds_ReturnsCategoryOverviewResponses() {
        Set<Long> ids = Set.of(1L, 2L);
        List<Category> categories = Arrays.asList(parentCategory, childCategory);

        when(categoryRepository.findAllById(ids)).thenReturn(categories);
        when(categoryMapper.toOverviewResponse(parentCategory)).thenReturn(parentOverviewResponse);
        when(categoryMapper.toOverviewResponse(childCategory)).thenReturn(categoryOverviewResponse);

        List<CategoryOverviewResponse> result = categoryService.getCategoriesByIds(ids);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(categoryValidator, times(1)).validateCategoriesExist(ids);
        verify(categoryRepository, times(1)).findAllById(ids);
        verify(categoryMapper, times(1)).toOverviewResponse(parentCategory);
        verify(categoryMapper, times(1)).toOverviewResponse(childCategory);
    }

    @Test
    void getAllCategories_ReturnsAllRootCategories() {
        List<Category> rootCategories = Collections.singletonList(parentCategory);
        when(categoryRepository.findByParentIsNull()).thenReturn(rootCategories);
        when(categoryMapper.toOverviewResponse(parentCategory)).thenReturn(parentOverviewResponse);

        List<CategoryOverviewResponse> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().id());
        verify(categoryRepository, times(1)).findByParentIsNull();
        verify(categoryMapper, times(1)).toOverviewResponse(parentCategory);
    }

    @Test
    void createCategory_ValidRequest_ReturnsNewCategoryId() {
        Category newCategory = Category.builder()
                .name("Test Category")
                .description("Test Description")
                .build();

        Category savedCategory = Category.builder()
                .id(3L)
                .name("Test Category")
                .description("Test Description")
                .parent(parentCategory)
                .build();

        when(categoryMapper.toCategory(categoryRequest)).thenReturn(newCategory);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        Long result = categoryService.createCategory(categoryRequest);

        assertEquals(3L, result);
        verify(categoryMapper, times(1)).toCategory(categoryRequest);
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryValidator, times(1)).validateNoInfiniteLoop(newCategory, parentCategory);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void updateCategory_ValidRequest_ReturnsUpdatedCategoryId() {
        Long categoryId = 2L;
        CategoryRequest updateRequest = new CategoryRequest(
                2L,
                "Updated Category",
                "Updated Description",
                1L
        );

        Category existingCategory = childCategory;

        Category updatedCategory = Category.builder()
                .id(2L)
                .name("Updated Category")
                .description("Updated Description")
                .build();

        Category savedCategory = Category.builder()
                .id(2L)
                .name("Updated Category")
                .description("Updated Description")
                .parent(parentCategory)
                .children(Collections.emptyList())
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryMapper.toCategory(updateRequest)).thenReturn(updatedCategory);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        Long result = categoryService.updateCategory(categoryId, updateRequest);

        assertEquals(2L, result);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, times(1)).toCategory(updateRequest);
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryValidator, times(1)).validateNoInfiniteLoop(updatedCategory, parentCategory);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void deleteCategoryById_ExistingId_DeletesCategory() {
        Long categoryId = 2L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(childCategory));

        categoryService.deleteCategoryById(categoryId);

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).delete(childCategory);
    }

    @Test
    void loadChildren_RecursivelyLoadsChildren() {
        Category parent = Category.builder()
                .id(1L)
                .name("Parent")
                .build();

        Category child = Category.builder()
                .id(2L)
                .name("Child")
                .parent(parent)
                .build();

        Category grandchild = Category.builder()
                .id(3L)
                .name("Grandchild")
                .parent(child)
                .build();

        parent.setChildren(Collections.singletonList(child));
        child.setChildren(Collections.singletonList(grandchild));
        grandchild.setChildren(Collections.emptyList());

        List<Category> rootCategories = Collections.singletonList(parent);
        when(categoryRepository.findByParentIsNull()).thenReturn(rootCategories);
        when(categoryMapper.toOverviewResponse(parent)).thenReturn(
                CategoryOverviewResponse.builder()
                        .id(1L)
                        .name("Parent")
                        .children(Collections.singletonList(
                                CategoryOverviewResponse.builder()
                                        .id(2L)
                                        .name("Child")
                                        .children(Collections.singletonList(
                                                CategoryOverviewResponse.builder()
                                                        .id(3L)
                                                        .name("Grandchild")
                                                        .children(Collections.emptyList())
                                                        .build()
                                        ))
                                        .build()
                        ))
                        .build()
        );

        List<CategoryOverviewResponse> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().id());
        assertEquals(1, result.getFirst().children().size());
        assertEquals(2L, result.getFirst().children().getFirst().id());
        assertEquals(1, result.getFirst().children().getFirst().children().size());
        assertEquals(3L, result.getFirst().children().getFirst().children().getFirst().id());
        verify(categoryRepository, times(1)).findByParentIsNull();
        verify(categoryMapper, times(1)).toOverviewResponse(parent);
    }
}