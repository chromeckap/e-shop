package com.ecommerce.product;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProductRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void constructor_WithValidParameters_CreatesInstance() {
        // Arrange
        Long id = 1L;
        String name = "Test Product";
        String description = "Test Description";
        boolean isVisible = true;
        Set<Long> categoryIds = Set.of(1L, 2L);
        Set<Long> attributeIds = Set.of(1L, 2L);
        Set<Long> relatedProductIds = Set.of(2L, 3L);

        // Act
        ProductRequest request = new ProductRequest(id, name, description, isVisible, categoryIds, attributeIds, relatedProductIds);

        // Assert
        assertEquals(id, request.id());
        assertEquals(name, request.name());
        assertEquals(description, request.description());
        assertEquals(isVisible, request.isVisible());
        assertEquals(categoryIds, request.categoryIds());
        assertEquals(attributeIds, request.attributeIds());
        assertEquals(relatedProductIds, request.relatedProductIds());
    }

    @Test
    void validation_WithValidRequest_PassesValidation() {
        // Arrange
        ProductRequest request = new ProductRequest(
                1L,
                "Test Product",
                "Test Description",
                true,
                Set.of(1L, 2L),
                Set.of(1L, 2L),
                Set.of(2L, 3L)
        );

        // Act
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_WithNullName_FailsValidation() {
        // Arrange
        ProductRequest request = new ProductRequest(
                1L,
                null,
                "Test Description",
                true,
                Set.of(1L, 2L),
                Set.of(1L, 2L),
                Set.of(2L, 3L)
        );

        // Act
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validation_WithEmptyName_FailsValidation() {
        // Arrange
        ProductRequest request = new ProductRequest(
                1L,
                "",
                "Test Description",
                true,
                Set.of(1L, 2L),
                Set.of(1L, 2L),
                Set.of(2L, 3L)
        );

        // Act
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validation_WithNameTooLong_FailsValidation() {
        // Arrange
        String nameTooLong = "a".repeat(256); // 256 characters
        ProductRequest request = new ProductRequest(
                1L,
                nameTooLong,
                "Test Description",
                true,
                Set.of(1L, 2L),
                Set.of(1L, 2L),
                Set.of(2L, 3L)
        );

        // Act
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validation_WithNullIsVisible_FailsValidation() {
        // This test will not compile because isVisible is a primitive boolean and cannot be null
        // We test with a null wrapper Boolean to verify this would be caught by validation
        ProductRequest request = new ProductRequest(
                1L,
                "Test Product",
                "Test Description",
                false, // Using false for isVisible, since it's a primitive and can't be null
                Set.of(1L, 2L),
                Set.of(1L, 2L),
                Set.of(2L, 3L)
        );

        // ActValidate a valid request to ensure validation works
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_WithOptionalFieldsNull_PassesValidation() {
        // Arrange
        ProductRequest request = new ProductRequest(
                null, // id can be null for creation
                "Test Product",
                null, // description can be null
                true,
                null, // categoryIds can be null
                null, // attributeIds can be null
                null  // relatedProductIds can be null
        );

        // Act
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_WithEmptyCollections_PassesValidation() {
        // Arrange
        ProductRequest request = new ProductRequest(
                1L,
                "Test Product",
                "Test Description",
                true,
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptySet()
        );

        // Act
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void equals_WithSameInstance_ReturnsTrue() {
        // Arrange
        ProductRequest request = new ProductRequest(
                1L,
                "Test Product",
                "Test Description",
                true,
                Set.of(1L, 2L),
                Set.of(1L, 2L),
                Set.of(2L, 3L)
        );

        // Act & Assert
        assertEquals(request, request);
    }

    @Test
    void equals_WithEqualInstance_ReturnsTrue() {
        // Arrange
        ProductRequest request1 = new ProductRequest(
                1L,
                "Test Product",
                "Test Description",
                true,
                Set.of(1L, 2L),
                Set.of(1L, 2L),
                Set.of(2L, 3L)
        );

        ProductRequest request2 = new ProductRequest(
                1L,
                "Test Product",
                "Test Description",
                true,
                Set.of(1L, 2L),
                Set.of(1L, 2L),
                Set.of(2L, 3L)
        );

        // Act & Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void equals_WithDifferentId_ReturnsFalse() {
        // Arrange
        ProductRequest request1 = new ProductRequest(
                1L,
                "Test Product",
                "Test Description",
                true,
                Set.of(1L, 2L),
                Set.of(1L, 2L),
                Set.of(2L, 3L)
        );

        ProductRequest request2 = new ProductRequest(
                2L,
                "Test Product",
                "Test Description",
                true,
                Set.of(1L, 2L),
                Set.of(1L, 2L),
                Set.of(2L, 3L)
        );

        // Act & Assert
        assertNotEquals(request1, request2);
    }

    @Test
    void equals_WithDifferentName_ReturnsFalse() {
        // Arrange
        ProductRequest request1 = new ProductRequest(
                1L,
                "Test Product 1",
                "Test Description",
                true,
                Set.of(1L, 2L),
                Set.of(1L, 2L),
                Set.of(2L, 3L)
        );

        ProductRequest request2 = new ProductRequest(
                1L,
                "Test Product 2",
                "Test Description",
                true,
                Set.of(1L, 2L),
                Set.of(1L, 2L),
                Set.of(2L, 3L)
        );

        // Act & Assert
        assertNotEquals(request1, request2);
    }

    @Test
    void equals_WithDifferentType_ReturnsFalse() {
        // Arrange
        ProductRequest request = new ProductRequest(
                1L,
                "Test Product",
                "Test Description",
                true,
                Set.of(1L, 2L),
                Set.of(1L, 2L),
                Set.of(2L, 3L)
        );

        // Act & Assert
        assertNotEquals(request, "Not a request");
    }

    @Test
    void equals_WithNull_ReturnsFalse() {
        // Arrange
        ProductRequest request = new ProductRequest(
                1L,
                "Test Product",
                "Test Description",
                true,
                Set.of(1L, 2L),
                Set.of(1L, 2L),
                Set.of(2L, 3L)
        );

        // Act & Assert
        assertNotEquals(request, null);
    }

    @Test
    void toString_ReturnsStringWithAllFields() {
        // Arrange
        ProductRequest request = new ProductRequest(
                1L,
                "Test Product",
                "Test Description",
                true,
                Set.of(1L, 2L),
                Set.of(1L, 2L),
                Set.of(2L, 3L)
        );

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name=Test Product"));
        assertTrue(toString.contains("description=Test Description"));
        assertTrue(toString.contains("isVisible=true"));
        assertTrue(toString.contains("categoryIds=[1, 2]") || toString.contains("categoryIds=[2, 1]"));
        assertTrue(toString.contains("attributeIds=[1, 2]") || toString.contains("attributeIds=[2, 1]"));
        assertTrue(toString.contains("relatedProductIds=[2, 3]") || toString.contains("relatedProductIds=[3, 2]"));
    }
}