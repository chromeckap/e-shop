package com.ecommerce.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoryMapperTest {

    private CategoryMapper categoryMapper;
    private Category parentCategory;
    private Category childCategory;
    private CategoryRequest categoryRequest;

    @BeforeEach
    void setUp() {
        categoryMapper = new CategoryMapper();

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
    }

    @Test
    void toCategory_ValidRequest_ReturnsCategory() {
        Category result = categoryMapper.toCategory(categoryRequest);

        assertNotNull(result);
        assertEquals(categoryRequest.id(), result.getId());
        assertEquals(categoryRequest.name(), result.getName());
        assertEquals(categoryRequest.description(), result.getDescription());
        assertNull(result.getParent());
        assertNull(result.getChildren());
    }

    @Test
    void toResponse_CategoryWithoutParentOrChildren_ReturnsCategoryResponse() {
        Category category = Category.builder()
                .id(3L)
                .name("Solo Category")
                .description("Solo Category Description")
                .build();

        CategoryResponse result = categoryMapper.toResponse(category);

        assertNotNull(result);
        assertEquals(3L, result.id());
        assertEquals("Solo Category", result.name());
        assertEquals("Solo Category Description", result.description());
        assertNull(result.parent());
        assertEquals(Collections.emptyList(), result.children());
    }

    @Test
    void toResponse_CategoryWithParentAndChildren_ReturnsCategoryResponse() {
        Category grandchild = Category.builder()
                .id(3L)
                .name("Grandchild Category")
                .description("Grandchild Category Description")
                .parent(childCategory)
                .children(Collections.emptyList())
                .build();

        List<Category> childrenList = Collections.singletonList(grandchild);
        childCategory.setChildren(childrenList);

        CategoryResponse result = categoryMapper.toResponse(childCategory);

        assertNotNull(result);
        assertEquals(2L, result.id());
        assertEquals("Child Category", result.name());
        assertEquals("Child Category Description", result.description());

        assertNotNull(result.parent());
        assertEquals(1L, result.parent().id());
        assertEquals("Parent Category", result.parent().name());

        assertNotNull(result.children());
        assertEquals(1, result.children().size());
        assertEquals(3L, result.children().getFirst().id());
        assertEquals("Grandchild Category", result.children().getFirst().name());
    }

    @Test
    void toOverviewResponse_CategoryWithoutChildren_ReturnsCategoryOverviewResponse() {
        CategoryOverviewResponse result = categoryMapper.toOverviewResponse(parentCategory);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Parent Category", result.name());
        assertEquals("Parent Category Description", result.description());
        assertEquals(Collections.emptyList(), result.children());
    }

    @Test
    void toOverviewResponse_CategoryWithChildren_ReturnsCategoryOverviewResponseWithChildren() {
        Category parent = Category.builder()
                .id(1L)
                .name("Parent")
                .description("Parent Description")
                .build();

        Category child1 = Category.builder()
                .id(2L)
                .name("Child 1")
                .description("Child 1 Description")
                .parent(parent)
                .children(Collections.emptyList())
                .build();

        Category child2 = Category.builder()
                .id(3L)
                .name("Child 2")
                .description("Child 2 Description")
                .parent(parent)
                .children(Collections.emptyList())
                .build();

        List<Category> children = Arrays.asList(child1, child2);
        parent.setChildren(children);

        CategoryOverviewResponse result = categoryMapper.toOverviewResponse(parent);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Parent", result.name());
        assertEquals("Parent Description", result.description());

        assertNotNull(result.children());
        assertEquals(2, result.children().size());

        assertEquals(2L, result.children().getFirst().id());
        assertEquals("Child 1", result.children().getFirst().name());
        assertEquals("Child 1 Description", result.children().get(0).description());
        assertEquals(Collections.emptyList(), result.children().get(0).children());

        assertEquals(3L, result.children().get(1).id());
        assertEquals("Child 2", result.children().get(1).name());
        assertEquals("Child 2 Description", result.children().get(1).description());
        assertEquals(Collections.emptyList(), result.children().get(1).children());
    }

    @Test
    void toOverviewResponse_CategoryWithNestedChildren_ReturnsNestedCategoryOverviewResponse() {
        Category parent = Category.builder()
                .id(1L)
                .name("Parent")
                .description("Parent Description")
                .build();

        Category child = Category.builder()
                .id(2L)
                .name("Child")
                .description("Child Description")
                .parent(parent)
                .build();

        Category grandchild = Category.builder()
                .id(3L)
                .name("Grandchild")
                .description("Grandchild Description")
                .parent(child)
                .children(Collections.emptyList())
                .build();

        child.setChildren(Collections.singletonList(grandchild));
        parent.setChildren(Collections.singletonList(child));

        CategoryOverviewResponse result = categoryMapper.toOverviewResponse(parent);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Parent", result.name());

        assertNotNull(result.children());
        assertEquals(1, result.children().size());
        assertEquals(2L, result.children().getFirst().id());
        assertEquals("Child", result.children().getFirst().name());

        assertNotNull(result.children().getFirst().children());
        assertEquals(1, result.children().getFirst().children().size());
        assertEquals(3L, result.children().getFirst().children().getFirst().id());
        assertEquals("Grandchild", result.children().getFirst().children().getFirst().name());
        assertEquals(Collections.emptyList(), result.children().getFirst().children().getFirst().children());
    }
}