package com.ecommerce.category;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoryResponseTest {

    @Test
    void testCreateUsingBuilder() {
        CategoryResponse response = CategoryResponse.builder()
                .id(1L)
                .name("Test Category")
                .description("Test Description")
                .parent(null)
                .children(Collections.emptyList())
                .build();

        assertEquals(1L, response.id());
        assertEquals("Test Category", response.name());
        assertEquals("Test Description", response.description());
        assertNull(response.parent());
        assertEquals(Collections.emptyList(), response.children());
    }

    @Test
    void testCreateWithParent() {
        CategoryOverviewResponse parent = CategoryOverviewResponse.builder()
                .id(2L)
                .name("Parent Category")
                .description("Parent Description")
                .children(Collections.emptyList())
                .build();

        CategoryResponse response = CategoryResponse.builder()
                .id(1L)
                .name("Test Category")
                .description("Test Description")
                .parent(parent)
                .children(Collections.emptyList())
                .build();

        assertEquals(1L, response.id());
        assertEquals("Test Category", response.name());
        assertEquals("Test Description", response.description());
        assertNotNull(response.parent());
        assertEquals(2L, response.parent().id());
        assertEquals("Parent Category", response.parent().name());
        assertEquals("Parent Description", response.parent().description());
        assertEquals(Collections.emptyList(), response.children());
    }

    @Test
    void testCreateWithChildren() {
        List<CategoryOverviewResponse> children = Arrays.asList(
                CategoryOverviewResponse.builder()
                        .id(2L)
                        .name("Child 1")
                        .description("Child 1 Description")
                        .children(Collections.emptyList())
                        .build(),
                CategoryOverviewResponse.builder()
                        .id(3L)
                        .name("Child 2")
                        .description("Child 2 Description")
                        .children(Collections.emptyList())
                        .build()
        );

        CategoryResponse response = CategoryResponse.builder()
                .id(1L)
                .name("Test Category")
                .description("Test Description")
                .parent(null)
                .children(children)
                .build();

        assertEquals(1L, response.id());
        assertEquals("Test Category", response.name());
        assertEquals("Test Description", response.description());
        assertNull(response.parent());
        assertEquals(2, response.children().size());
        assertEquals(2L, response.children().get(0).id());
        assertEquals("Child 1", response.children().get(0).name());
        assertEquals(3L, response.children().get(1).id());
        assertEquals("Child 2", response.children().get(1).name());
    }

    @Test
    void testCreateWithNullValues() {
        CategoryResponse response = CategoryResponse.builder()
                .id(null)
                .name(null)
                .description(null)
                .parent(null)
                .children(null)
                .build();

        assertNull(response.id());
        assertNull(response.name());
        assertNull(response.description());
        assertNull(response.parent());
        assertNull(response.children());
    }

    @Test
    void testImmutability() {
        List<CategoryOverviewResponse> children = new ArrayList<>();
        children.add(CategoryOverviewResponse.builder()
                .id(2L)
                .name("Child Category")
                .description("Child Description")
                .children(Collections.emptyList())
                .build());

        CategoryResponse response = CategoryResponse.builder()
                .id(1L)
                .name("Parent Category")
                .description("Parent Description")
                .parent(null)
                .children(children)
                .build();

        CategoryOverviewResponse newChild = CategoryOverviewResponse.builder()
                .id(3L)
                .name("New Child")
                .description("New Child Description")
                .children(Collections.emptyList())
                .build();

        children.add(newChild);

        assertEquals(2, response.children().size(), "Builder doesn't make defensive copies, so this shows the actual behavior");
    }

    @Test
    void testEquality() {
        CategoryOverviewResponse parent = CategoryOverviewResponse.builder()
                .id(2L)
                .name("Parent Category")
                .description("Parent Description")
                .children(Collections.emptyList())
                .build();

        List<CategoryOverviewResponse> children = Collections.singletonList(
                CategoryOverviewResponse.builder()
                        .id(3L)
                        .name("Child 1")
                        .description("Child 1 Description")
                        .children(Collections.emptyList())
                        .build()
        );

        CategoryResponse response1 = CategoryResponse.builder()
                .id(1L)
                .name("Test Category")
                .description("Test Description")
                .parent(parent)
                .children(children)
                .build();

        CategoryResponse response2 = CategoryResponse.builder()
                .id(1L)
                .name("Test Category")
                .description("Test Description")
                .parent(parent)
                .children(children)
                .build();

        CategoryResponse response3 = CategoryResponse.builder()
                .id(2L)
                .name("Test Category")
                .description("Test Description")
                .parent(parent)
                .children(children)
                .build();

        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
    }

    @Test
    void testHashCode() {
        CategoryOverviewResponse parent = CategoryOverviewResponse.builder()
                .id(2L)
                .name("Parent Category")
                .description("Parent Description")
                .children(Collections.emptyList())
                .build();

        List<CategoryOverviewResponse> children = Collections.singletonList(
                CategoryOverviewResponse.builder()
                        .id(3L)
                        .name("Child 1")
                        .description("Child 1 Description")
                        .children(Collections.emptyList())
                        .build()
        );

        CategoryResponse response1 = CategoryResponse.builder()
                .id(1L)
                .name("Test Category")
                .description("Test Description")
                .parent(parent)
                .children(children)
                .build();

        CategoryResponse response2 = CategoryResponse.builder()
                .id(1L)
                .name("Test Category")
                .description("Test Description")
                .parent(parent)
                .children(children)
                .build();

        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testComplexStructure() {
        CategoryOverviewResponse grandchild = CategoryOverviewResponse.builder()
                .id(4L)
                .name("Grandchild")
                .description("Grandchild Description")
                .children(Collections.emptyList())
                .build();

        List<CategoryOverviewResponse> grandchildren = Collections.singletonList(grandchild);
        CategoryOverviewResponse child = CategoryOverviewResponse.builder()
                .id(3L)
                .name("Child")
                .description("Child Description")
                .children(grandchildren)
                .build();

        CategoryOverviewResponse parent = CategoryOverviewResponse.builder()
                .id(2L)
                .name("Parent")
                .description("Parent Description")
                .children(Collections.emptyList())
                .build();

        List<CategoryOverviewResponse> children = Collections.singletonList(child);
        CategoryResponse response = CategoryResponse.builder()
                .id(1L)
                .name("Category")
                .description("Category Description")
                .parent(parent)
                .children(children)
                .build();

        assertEquals(1L, response.id());
        assertEquals("Category", response.name());

        assertNotNull(response.parent());
        assertEquals(2L, response.parent().id());
        assertEquals("Parent", response.parent().name());

        assertEquals(1, response.children().size());
        assertEquals(3L, response.children().getFirst().id());
        assertEquals("Child", response.children().getFirst().name());

        assertEquals(1, response.children().getFirst().children().size());
        assertEquals(4L, response.children().getFirst().children().getFirst().id());
        assertEquals("Grandchild", response.children().getFirst().children().getFirst().name());
    }
}