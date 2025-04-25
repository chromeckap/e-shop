package com.ecommerce.category;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void testCategoryBuilder() {
        Category category = Category.builder()
                .id(1L)
                .name("Test Category")
                .description("Category Description")
                .build();

        assertEquals(1L, category.getId());
        assertEquals("Test Category", category.getName());
        assertEquals("Category Description", category.getDescription());
        assertNull(category.getParent());
        assertNull(category.getChildren());
    }

    @Test
    void testCategoryConstructor() {
        List<Category> children = new ArrayList<>();
        Category parent = Category.builder().id(2L).name("Parent Category").build();

        Category category = new Category(1L, "Test Category", "Category Description", parent, children);

        assertEquals(1L, category.getId());
        assertEquals("Test Category", category.getName());
        assertEquals("Category Description", category.getDescription());
        assertSame(parent, category.getParent());
        assertSame(children, category.getChildren());
    }

    @Test
    void testNoArgsConstructor() {
        Category category = new Category();

        assertNull(category.getId());
        assertNull(category.getName());
        assertNull(category.getDescription());
        assertNull(category.getParent());
        assertNull(category.getChildren());
    }

    @Test
    void testSettersAndGetters() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        category.setDescription("Category Description");

        Category parent = Category.builder().id(2L).name("Parent Category").build();
        category.setParent(parent);

        List<Category> children = new ArrayList<>();
        Category child = Category.builder().id(3L).name("Child Category").build();
        children.add(child);
        category.setChildren(children);

        assertEquals(1L, category.getId());
        assertEquals("Test Category", category.getName());
        assertEquals("Category Description", category.getDescription());
        assertSame(parent, category.getParent());
        assertSame(children, category.getChildren());
        assertEquals(1, category.getChildren().size());
        assertSame(child, category.getChildren().getFirst());
    }

    @Test
    void testParentChildRelationship() {
        Category parent = Category.builder()
                .id(1L)
                .name("Parent Category")
                .description("Parent Description")
                .build();

        Category child1 = Category.builder()
                .id(2L)
                .name("Child Category 1")
                .description("Child Description 1")
                .parent(parent)
                .build();

        Category child2 = Category.builder()
                .id(3L)
                .name("Child Category 2")
                .description("Child Description 2")
                .parent(parent)
                .build();

        List<Category> children = Arrays.asList(child1, child2);
        parent.setChildren(children);

        assertEquals(2, parent.getChildren().size());
        assertTrue(parent.getChildren().contains(child1));
        assertTrue(parent.getChildren().contains(child2));

        assertSame(parent, child1.getParent());
        assertSame(parent, child2.getParent());
    }

    @Test
    void testEqualsAndHashCode() {
        Category category1 = Category.builder().id(1L).name("Category 1").build();
        Category category2 = Category.builder().id(1L).name("Different Name").build();
        Category category3 = Category.builder().id(2L).name("Category 1").build();

        assertEquals(category1, category2);
        assertEquals(category1.hashCode(), category2.hashCode());

        assertNotEquals(category1, category3);
        assertNotEquals(category1.hashCode(), category3.hashCode());
    }

    @Test
    void testToString() {
        Category category = Category.builder()
                .id(1L)
                .name("Test Category")
                .description("Category Description")
                .build();

        String toString = category.toString();

        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name=Test Category"));
        assertTrue(toString.contains("description=Category Description"));
    }

    @Test
    void testDeepNestedStructure() {
        Category root = Category.builder()
                .id(1L)
                .name("Root")
                .build();

        Category level1 = Category.builder()
                .id(2L)
                .name("Level 1")
                .parent(root)
                .build();

        Category level2 = Category.builder()
                .id(3L)
                .name("Level 2")
                .parent(level1)
                .build();

        Category level3 = Category.builder()
                .id(4L)
                .name("Level 3")
                .parent(level2)
                .build();

        root.setChildren(Collections.singletonList(level1));
        level1.setChildren(Collections.singletonList(level2));
        level2.setChildren(Collections.singletonList(level3));
        level3.setChildren(Collections.emptyList());

        assertSame(root, level1.getParent());
        assertSame(level1, level2.getParent());
        assertSame(level2, level3.getParent());

        assertEquals(1, root.getChildren().size());
        assertEquals(1, level1.getChildren().size());
        assertEquals(1, level2.getChildren().size());
        assertEquals(0, level3.getChildren().size());

        assertSame(level1, root.getChildren().getFirst());
        assertSame(level2, level1.getChildren().getFirst());
        assertSame(level3, level2.getChildren().getFirst());
    }
}