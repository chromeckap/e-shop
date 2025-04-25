package com.ecommerce.variant;

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

class VariantRequestTest {

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
        Long productId = 1L;
        String sku = "TEST-SKU";
        BigDecimal basePrice = new BigDecimal("100.00");
        BigDecimal discountedPrice = new BigDecimal("80.00");
        int quantity = 10;
        boolean quantityUnlimited = false;
        Set<Long> attributeValueIds = Set.of(1L, 2L);

        // Act
        VariantRequest request = new VariantRequest(
                id, productId, sku, basePrice, discountedPrice, quantity, quantityUnlimited, attributeValueIds);

        // Assert
        assertEquals(id, request.id());
        assertEquals(productId, request.productId());
        assertEquals(sku, request.sku());
        assertEquals(basePrice, request.basePrice());
        assertEquals(discountedPrice, request.discountedPrice());
        assertEquals(quantity, request.quantity());
        assertEquals(quantityUnlimited, request.quantityUnlimited());
        assertEquals(attributeValueIds, request.attributeValueIds());
    }

    @Test
    void validation_WithValidRequest_PassesValidation() {
        // Arrange
        VariantRequest request = new VariantRequest(
                1L, 1L, "TEST-SKU", new BigDecimal("100.00"), new BigDecimal("80.00"), 10, false, Set.of(1L, 2L));

        // Act
        Set<ConstraintViolation<VariantRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_WithNullProductId_FailsValidation() {
        // Arrange
        VariantRequest request = new VariantRequest(
                1L, null, "TEST-SKU", new BigDecimal("100.00"), new BigDecimal("80.00"), 10, false, Set.of(1L, 2L));

        // Act
        Set<ConstraintViolation<VariantRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("productId", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validation_WithNullSku_FailsValidation() {
        // Arrange
        VariantRequest request = new VariantRequest(
                1L, 1L, null, new BigDecimal("100.00"), new BigDecimal("80.00"), 10, false, Set.of(1L, 2L));

        // Act
        Set<ConstraintViolation<VariantRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("sku", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validation_WithEmptySku_FailsValidation() {
        // Arrange
        VariantRequest request = new VariantRequest(
                1L, 1L, "", new BigDecimal("100.00"), new BigDecimal("80.00"), 10, false, Set.of(1L, 2L));

        // Act
        Set<ConstraintViolation<VariantRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("sku", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validation_WithSkuTooLong_FailsValidation() {
        // Arrange
        String skuTooLong = "a".repeat(51); // 51 characters, max is 50
        VariantRequest request = new VariantRequest(
                1L, 1L, skuTooLong, new BigDecimal("100.00"), new BigDecimal("80.00"), 10, false, Set.of(1L, 2L));

        // Act
        Set<ConstraintViolation<VariantRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("sku", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validation_WithNullBasePrice_FailsValidation() {
        // Arrange
        VariantRequest request = new VariantRequest(
                1L, 1L, "TEST-SKU", null, new BigDecimal("80.00"), 10, false, Set.of(1L, 2L));

        // Act
        Set<ConstraintViolation<VariantRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("basePrice", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validation_WithNegativeBasePrice_FailsValidation() {
        // Arrange
        VariantRequest request = new VariantRequest(
                1L, 1L, "TEST-SKU", new BigDecimal("-100.00"), new BigDecimal("80.00"), 10, false, Set.of(1L, 2L));

        // Act
        Set<ConstraintViolation<VariantRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("basePrice", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validation_WithZeroBasePrice_PassesValidation() {
        // Arrange
        VariantRequest request = new VariantRequest(
                1L, 1L, "TEST-SKU", BigDecimal.ZERO, new BigDecimal("80.00"), 10, false, Set.of(1L, 2L));

        // Act
        Set<ConstraintViolation<VariantRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_WithNegativeDiscountedPrice_FailsValidation() {
        // Arrange
        VariantRequest request = new VariantRequest(
                1L, 1L, "TEST-SKU", new BigDecimal("100.00"), new BigDecimal("-80.00"), 10, false, Set.of(1L, 2L));

        // Act
        Set<ConstraintViolation<VariantRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("discountedPrice", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validation_WithNullDiscountedPrice_PassesValidation() {
        // Arrange
        VariantRequest request = new VariantRequest(
                1L, 1L, "TEST-SKU", new BigDecimal("100.00"), null, 10, false, Set.of(1L, 2L));

        // Act
        Set<ConstraintViolation<VariantRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_WithNegativeQuantity_FailsValidation() {
        // Arrange
        VariantRequest request = new VariantRequest(
                1L, 1L, "TEST-SKU", new BigDecimal("100.00"), new BigDecimal("80.00"), -10, false, Set.of(1L, 2L));

        // Act
        Set<ConstraintViolation<VariantRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("quantity", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validation_WithZeroQuantity_PassesValidation() {
        // Arrange
        VariantRequest request = new VariantRequest(
                1L, 1L, "TEST-SKU", new BigDecimal("100.00"), new BigDecimal("80.00"), 0, false, Set.of(1L, 2L));

        // Act
        Set<ConstraintViolation<VariantRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_WithNullAttributeValueIds_PassesValidation() {
        // Arrange
        VariantRequest request = new VariantRequest(
                1L, 1L, "TEST-SKU", new BigDecimal("100.00"), new BigDecimal("80.00"), 10, false, null);

        // Act
        Set<ConstraintViolation<VariantRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_WithEmptyAttributeValueIds_PassesValidation() {
        // Arrange
        VariantRequest request = new VariantRequest(
                1L, 1L, "TEST-SKU", new BigDecimal("100.00"), new BigDecimal("80.00"), 10, false, Collections.emptySet());

        // Act
        Set<ConstraintViolation<VariantRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void equals_WithSameInstance_ReturnsTrue() {
        // Arrange
        VariantRequest request = new VariantRequest(
                1L, 1L, "TEST-SKU", new BigDecimal("100.00"), new BigDecimal("80.00"), 10, false, Set.of(1L, 2L));

        // Act & Assert
        assertEquals(request, request);
    }

    @Test
    void equals_WithEqualInstance_ReturnsTrue() {
        // Arrange
        VariantRequest request1 = new VariantRequest(
                1L, 1L, "TEST-SKU", new BigDecimal("100.00"), new BigDecimal("80.00"), 10, false, Set.of(1L, 2L));

        VariantRequest request2 = new VariantRequest(
                1L, 1L, "TEST-SKU", new BigDecimal("100.00"), new BigDecimal("80.00"), 10, false, Set.of(1L, 2L));

        // Act & Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void equals_WithDifferentId_ReturnsFalse() {
        // Arrange
        VariantRequest request1 = new VariantRequest(
                1L, 1L, "TEST-SKU", new BigDecimal("100.00"), new BigDecimal("80.00"), 10, false, Set.of(1L, 2L));

        VariantRequest request2 = new VariantRequest(
                2L, 1L, "TEST-SKU", new BigDecimal("100.00"), new BigDecimal("80.00"), 10, false, Set.of(1L, 2L));

        // Act & Assert
        assertNotEquals(request1, request2);
    }

    @Test
    void toString_ReturnsStringWithAllFields() {
        // Arrange
        VariantRequest request = new VariantRequest(
                1L, 1L, "TEST-SKU", new BigDecimal("100.00"), new BigDecimal("80.00"), 10, false, Set.of(1L, 2L));

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("productId=1"));
        assertTrue(toString.contains("sku=TEST-SKU"));
        assertTrue(toString.contains("basePrice=100.00"));
        assertTrue(toString.contains("discountedPrice=80.00"));
        assertTrue(toString.contains("quantity=10"));
        assertTrue(toString.contains("quantityUnlimited=false"));
        assertTrue(toString.contains("attributeValueIds=[1, 2]") || toString.contains("attributeValueIds=[2, 1]"));
    }
}