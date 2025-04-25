package com.ecommerce.attributevalue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AttributeValueRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void constructor_WithValidParameters_CreatesInstance() {
        // Arrange
        Long id = 1L;
        String value = "Red";

        // Act
        AttributeValueRequest request = new AttributeValueRequest(id, value);

        // Assert
        assertEquals(id, request.id());
        assertEquals(value, request.value());
    }

    @Test
    void validation_WithValidRequest_PassesValidation() {
        // Arrange
        AttributeValueRequest request = new AttributeValueRequest(1L, "Red");

        // Act
        Set<ConstraintViolation<AttributeValueRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_WithNullValue_FailsValidation() {
        // Arrange
        AttributeValueRequest request = new AttributeValueRequest(1L, null);

        // Act
        Set<ConstraintViolation<AttributeValueRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("value", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validation_WithEmptyValue_FailsValidation() {
        // Arrange
        AttributeValueRequest request = new AttributeValueRequest(1L, "");

        // Act
        Set<ConstraintViolation<AttributeValueRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("value", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validation_WithBlankValue_FailsValidation() {
        // Arrange
        AttributeValueRequest request = new AttributeValueRequest(1L, "   ");

        // Act
        Set<ConstraintViolation<AttributeValueRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("value", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validation_WithValueTooLong_FailsValidation() {
        // Arrange
        String tooLongValue = "a".repeat(101); // 101 characters
        AttributeValueRequest request = new AttributeValueRequest(1L, tooLongValue);

        // Act
        Set<ConstraintViolation<AttributeValueRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("value", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validation_WithExactlyMaxLengthValue_PassesValidation() {
        // Arrange
        String maxLengthValue = "a".repeat(100); // 100 characters
        AttributeValueRequest request = new AttributeValueRequest(1L, maxLengthValue);

        // Act
        Set<ConstraintViolation<AttributeValueRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void equals_WithSameInstance_ReturnsTrue() {
        // Arrange
        AttributeValueRequest request = new AttributeValueRequest(1L, "Red");

        // Act & Assert
        assertEquals(request, request);
    }

    @Test
    void equals_WithEqualInstance_ReturnsTrue() {
        // Arrange
        AttributeValueRequest request1 = new AttributeValueRequest(1L, "Red");
        AttributeValueRequest request2 = new AttributeValueRequest(1L, "Red");

        // Act & Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void equals_WithDifferentId_ReturnsFalse() {
        // Arrange
        AttributeValueRequest request1 = new AttributeValueRequest(1L, "Red");
        AttributeValueRequest request2 = new AttributeValueRequest(2L, "Red");

        // Act & Assert
        assertNotEquals(request1, request2);
    }

    @Test
    void equals_WithDifferentValue_ReturnsFalse() {
        // Arrange
        AttributeValueRequest request1 = new AttributeValueRequest(1L, "Red");
        AttributeValueRequest request2 = new AttributeValueRequest(1L, "Blue");

        // Act & Assert
        assertNotEquals(request1, request2);
    }

    @Test
    void equals_WithNullId_ComparesProperly() {
        // Arrange
        AttributeValueRequest request1 = new AttributeValueRequest(null, "Red");
        AttributeValueRequest request2 = new AttributeValueRequest(null, "Red");
        AttributeValueRequest request3 = new AttributeValueRequest(1L, "Red");

        // Act & Assert
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
    }

    @Test
    void equals_WithDifferentType_ReturnsFalse() {
        // Arrange
        AttributeValueRequest request = new AttributeValueRequest(1L, "Red");

        // Act & Assert
        assertNotEquals(request, "Not a request");
    }

    @Test
    void equals_WithNull_ReturnsFalse() {
        // Arrange
        AttributeValueRequest request = new AttributeValueRequest(1L, "Red");

        // Act & Assert
        assertNotEquals(request, null);
    }

    @Test
    void toString_ReturnsStringWithAllFields() {
        // Arrange
        AttributeValueRequest request = new AttributeValueRequest(1L, "Red");

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("value=Red"));
    }
}