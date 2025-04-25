package com.ecommerce.product;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProductSpecificationRequestTest {

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
        BigDecimal lowPrice = new BigDecimal("10.00");
        BigDecimal maxPrice = new BigDecimal("100.00");
        Set<Long> attributeValueIds = Set.of(1L, 2L, 3L);

        // Act
        ProductSpecificationRequest request = new ProductSpecificationRequest(lowPrice, maxPrice, attributeValueIds);

        // Assert
        assertEquals(lowPrice, request.lowPrice());
        assertEquals(maxPrice, request.maxPrice());
        assertEquals(attributeValueIds, request.attributeValueIds());
    }

    @Test
    void validation_WithValidRequest_PassesValidation() {
        // Arrange
        ProductSpecificationRequest request = new ProductSpecificationRequest(
                new BigDecimal("10.00"),
                new BigDecimal("100.00"),
                Set.of(1L, 2L, 3L)
        );

        // Act
        Set<ConstraintViolation<ProductSpecificationRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_WithNegativeLowPrice_FailsValidation() {
        // Arrange
        ProductSpecificationRequest request = new ProductSpecificationRequest(
                new BigDecimal("-10.00"),
                new BigDecimal("100.00"),
                Set.of(1L, 2L, 3L)
        );

        // Act
        Set<ConstraintViolation<ProductSpecificationRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("lowPrice", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validation_WithZeroLowPrice_PassesValidation() {
        // Arrange
        ProductSpecificationRequest request = new ProductSpecificationRequest(
                BigDecimal.ZERO,
                new BigDecimal("100.00"),
                Set.of(1L, 2L, 3L)
        );

        // Act
        Set<ConstraintViolation<ProductSpecificationRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_WithZeroMaxPrice_FailsValidation() {
        // Arrange
        ProductSpecificationRequest request = new ProductSpecificationRequest(
                new BigDecimal("10.00"),
                BigDecimal.ZERO,
                Set.of(1L, 2L, 3L)
        );

        // Act
        Set<ConstraintViolation<ProductSpecificationRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("maxPrice", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validation_WithNegativeMaxPrice_FailsValidation() {
        // Arrange
        ProductSpecificationRequest request = new ProductSpecificationRequest(
                new BigDecimal("10.00"),
                new BigDecimal("-100.00"),
                Set.of(1L, 2L, 3L)
        );

        // Act
        Set<ConstraintViolation<ProductSpecificationRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("maxPrice", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validation_WithOptionalFieldsNull_PassesValidation() {
        // Arrange
        ProductSpecificationRequest request = new ProductSpecificationRequest(
                null, // lowPrice can be null
                null, // maxPrice can be null
                null  // attributeValueIds can be null
        );

        // Act
        Set<ConstraintViolation<ProductSpecificationRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_WithEmptyAttributeValueIds_PassesValidation() {
        // Arrange
        ProductSpecificationRequest request = new ProductSpecificationRequest(
                new BigDecimal("10.00"),
                new BigDecimal("100.00"),
                Collections.emptySet()
        );

        // Act
        Set<ConstraintViolation<ProductSpecificationRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void equals_WithSameInstance_ReturnsTrue() {
        // Arrange
        ProductSpecificationRequest request = new ProductSpecificationRequest(
                new BigDecimal("10.00"),
                new BigDecimal("100.00"),
                Set.of(1L, 2L, 3L)
        );

        // Act & Assert
        assertEquals(request, request);
    }

    @Test
    void equals_WithEqualInstance_ReturnsTrue() {
        // Arrange
        ProductSpecificationRequest request1 = new ProductSpecificationRequest(
                new BigDecimal("10.00"),
                new BigDecimal("100.00"),
                Set.of(1L, 2L, 3L)
        );

        ProductSpecificationRequest request2 = new ProductSpecificationRequest(
                new BigDecimal("10.00"),
                new BigDecimal("100.00"),
                Set.of(1L, 2L, 3L)
        );

        // Act & Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void equals_WithDifferentLowPrice_ReturnsFalse() {
        // Arrange
        ProductSpecificationRequest request1 = new ProductSpecificationRequest(
                new BigDecimal("10.00"),
                new BigDecimal("100.00"),
                Set.of(1L, 2L, 3L)
        );

        ProductSpecificationRequest request2 = new ProductSpecificationRequest(
                new BigDecimal("20.00"),
                new BigDecimal("100.00"),
                Set.of(1L, 2L, 3L)
        );

        // Act & Assert
        assertNotEquals(request1, request2);
    }

    @Test
    void equals_WithDifferentMaxPrice_ReturnsFalse() {
        // Arrange
        ProductSpecificationRequest request1 = new ProductSpecificationRequest(
                new BigDecimal("10.00"),
                new BigDecimal("100.00"),
                Set.of(1L, 2L, 3L)
        );

        ProductSpecificationRequest request2 = new ProductSpecificationRequest(
                new BigDecimal("10.00"),
                new BigDecimal("200.00"),
                Set.of(1L, 2L, 3L)
        );

        // Act & Assert
        assertNotEquals(request1, request2);
    }

    @Test
    void equals_WithDifferentAttributeValueIds_ReturnsFalse() {
        // Arrange
        ProductSpecificationRequest request1 = new ProductSpecificationRequest(
                new BigDecimal("10.00"),
                new BigDecimal("100.00"),
                Set.of(1L, 2L, 3L)
        );

        ProductSpecificationRequest request2 = new ProductSpecificationRequest(
                new BigDecimal("10.00"),
                new BigDecimal("100.00"),
                Set.of(4L, 5L, 6L)
        );

        // Act & Assert
        assertNotEquals(request1, request2);
    }

    @Test
    void equals_WithDifferentType_ReturnsFalse() {
        // Arrange
        ProductSpecificationRequest request = new ProductSpecificationRequest(
                new BigDecimal("10.00"),
                new BigDecimal("100.00"),
                Set.of(1L, 2L, 3L)
        );

        // Act & Assert
        assertNotEquals(request, "Not a request");
    }

    @Test
    void equals_WithNull_ReturnsFalse() {
        // Arrange
        ProductSpecificationRequest request = new ProductSpecificationRequest(
                new BigDecimal("10.00"),
                new BigDecimal("100.00"),
                Set.of(1L, 2L, 3L)
        );

        // Act & Assert
        assertNotEquals(request, null);
    }

    @Test
    void toString_ReturnsStringWithAllFields() {
        // Arrange
        ProductSpecificationRequest request = new ProductSpecificationRequest(
                new BigDecimal("10.00"),
                new BigDecimal("100.00"),
                Set.of(1L, 2L, 3L)
        );

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("lowPrice=10.00"));
        assertTrue(toString.contains("maxPrice=100.00"));
        assertTrue(toString.contains("attributeValueIds=[1, 2, 3]")
                || toString.contains("attributeValueIds=[1, 3, 2]")
                || toString.contains("attributeValueIds=[2, 1, 3]")
                || toString.contains("attributeValueIds=[2, 3, 1]")
                || toString.contains("attributeValueIds=[3, 1, 2]")
                || toString.contains("attributeValueIds=[3, 2, 1]"));
    }
}