package com.ecommerce.category;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoryOverviewResponseTest {

    @Test
    void testCreateUsingBuilder() {
        CategoryOverviewResponse response = CategoryOverviewResponse.builder()
                .id(1L)
                .name("Test Category")
                .description("Test Description")
                .children(Collections.emptyList())
                .build();

        assertEquals(1L, response.id());
        assertEquals("Test Category", response.name());
        assertEquals("Test Description", response.description());
        assertEquals(Collections.emptyList(), response.children());
    }

    @Test
    void testCreateWithNullValues() {
        CategoryOverviewResponse response = CategoryOverviewResponse.builder()
                .id(null)
                .name(null)
                .description(null)
                .children(null)
                .build();

        assertNull(response.id());
        assertNull(response.name());
        assertNull(response.description());
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

        CategoryOverviewResponse response = CategoryOverviewResponse.builder()
                .id(1L)
                .name("Parent Category")
                .description("Parent Description")
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
        CategoryOverviewResponse response1 = CategoryOverviewResponse.builder()
                .id(1L)
                .name("Test Category")
                .description("Test Description")
                .children(Collections.emptyList())
                .build();

        CategoryOverviewResponse response2 = CategoryOverviewResponse.builder()
                .id(1L)
                .name("Test Category")
                .description("Test Description")
                .children(Collections.emptyList())
                .build();

        CategoryOverviewResponse response3 = CategoryOverviewResponse.builder()
                .id(2L)
                .name("Test Category")
                .description("Test Description")
                .children(Collections.emptyList())
                .build();

        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
    }

    @Test
    void testHashCode() {
        CategoryOverviewResponse response1 = CategoryOverviewResponse.builder()
                .id(1L)
                .name("Test Category")
                .description("Test Description")
                .children(Collections.emptyList())
                .build();

        CategoryOverviewResponse response2 = CategoryOverviewResponse.builder()
                .id(1L)
                .name("Test Category")
                .description("Test Description")
                .children(Collections.emptyList())
                .build();

        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testNestedStructure() {
        CategoryOverviewResponse childResponse = CategoryOverviewResponse.builder()
                .id(2L)
                .name("Child Category")
                .description("Child Description")
                .children(Collections.emptyList())
                .build();

        CategoryOverviewResponse grandchildResponse = CategoryOverviewResponse.builder()
                .id(3L)
                .name("Grandchild Category")
                .description("Grandchild Description")
                .children(Collections.emptyList())
                .build();

        CategoryOverviewResponse parentResponse = CategoryOverviewResponse.builder()
                .id(1L)
                .name("Parent Category")
                .description("Parent Description")
                .children(Collections.singletonList(childResponse))
                .build();

        assertEquals(1L, parentResponse.id());
        assertEquals(1, parentResponse.children().size());
        assertEquals(2L, parentResponse.children().getFirst().id());
        assertEquals("Child Category", parentResponse.children().getFirst().name());
        assertEquals(0, parentResponse.children().getFirst().children().size());

        CategoryOverviewResponse updatedChildResponse = CategoryOverviewResponse.builder()
                .id(2L)
                .name("Child Category")
                .description("Child Description")
                .children(Collections.singletonList(grandchildResponse))
                .build();

        CategoryOverviewResponse updatedParentResponse = CategoryOverviewResponse.builder()
                .id(1L)
                .name("Parent Category")
                .description("Parent Description")
                .children(Collections.singletonList(updatedChildResponse))
                .build();

        assertEquals(1, updatedParentResponse.children().size());
        assertEquals(1, updatedParentResponse.children().getFirst().children().size());
        assertEquals(3L, updatedParentResponse.children().getFirst().children().getFirst().id());
        assertEquals("Grandchild Category", updatedParentResponse.children().getFirst().children().getFirst().name());
    }
}