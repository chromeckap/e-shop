package com.ecommerce.attribute;

import com.ecommerce.attributevalue.AttributeValueRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AttributeRequestTest {

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
        String name = "Color";
        List<AttributeValueRequest> values = List.of(new AttributeValueRequest(1L, "Red"));

        // Act
        AttributeRequest request = new AttributeRequest(id, name, values);

        // Assert
        assertEquals(id, request.id());
        assertEquals(name, request.name());
        assertEquals(values, request.values());
    }

    @Test
    void validation_WithValidRequest_PassesValidation() {
        // Arrange
        AttributeRequest request = new AttributeRequest(
                1L,
                "Color",
                List.of(new AttributeValueRequest(1L, "Red"))
        );

        // Act
        Set<ConstraintViolation<AttributeRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_WithNullName_FailsValidation() {
        // Arrange
        AttributeRequest request = new AttributeRequest(
                1L,
                null,
                List.of(new AttributeValueRequest(1L, "Red"))
        );

        // Act
        Set<ConstraintViolation<AttributeRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validation_WithEmptyName_FailsValidation() {
        // Arrange
        AttributeRequest request = new AttributeRequest(
                1L,
                "",
                List.of(new AttributeValueRequest(1L, "Red"))
        );

        // Act
        Set<ConstraintViolation<AttributeRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validation_WithBlankName_FailsValidation() {
        // Arrange
        AttributeRequest request = new AttributeRequest(
                1L,
                "   ",
                List.of(new AttributeValueRequest(1L, "Red"))
        );

        // Act
        Set<ConstraintViolation<AttributeRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validation_WithNameTooLong_FailsValidation() {
        // Arrange
        String tooLongName = "a".repeat(101); // 101 characters
        AttributeRequest request = new AttributeRequest(
                1L,
                tooLongName,
                List.of(new AttributeValueRequest(1L, "Red"))
        );

        // Act
        Set<ConstraintViolation<AttributeRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validation_WithExactlyMaxLengthName_PassesValidation() {
        // Arrange
        String maxLengthName = "a".repeat(100); // 100 characters
        AttributeRequest request = new AttributeRequest(
                1L,
                maxLengthName,
                List.of(new AttributeValueRequest(1L, "Red"))
        );

        // Act
        Set<ConstraintViolation<AttributeRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_WithNullValues_FailsValidation() {
        // Arrange
        AttributeRequest request = new AttributeRequest(
                1L,
                "Color",
                null
        );

        // Act
        Set<ConstraintViolation<AttributeRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("values", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validation_WithEmptyValues_PassesValidation() {
        // Arrange
        AttributeRequest request = new AttributeRequest(
                1L,
                "Color",
                Collections.emptyList()
        );

        // Act
        Set<ConstraintViolation<AttributeRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void equals_WithSameInstance_ReturnsTrue() {
        // Arrange
        AttributeRequest request = new AttributeRequest(
                1L,
                "Color",
                List.of(new AttributeValueRequest(1L, "Red"))
        );

        // Act & Assert
        assertEquals(request, request);
    }

    @Test
    void equals_WithEqualInstance_ReturnsTrue() {
        // Arrange
        AttributeRequest request1 = new AttributeRequest(
                1L,
                "Color",
                List.of(new AttributeValueRequest(1L, "Red"))
        );

        AttributeRequest request2 = new AttributeRequest(
                1L,
                "Color",
                List.of(new AttributeValueRequest(1L, "Red"))
        );

        // Act & Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void equals_WithDifferentId_ReturnsFalse() {
        // Arrange
        AttributeRequest request1 = new AttributeRequest(
                1L,
                "Color",
                List.of(new AttributeValueRequest(1L, "Red"))
        );

        AttributeRequest request2 = new AttributeRequest(
                2L,
                "Color",
                List.of(new AttributeValueRequest(1L, "Red"))
        );

        // Act & Assert
        assertNotEquals(request1, request2);
    }

    @Test
    void equals_WithNullId_ComparesProperly() {
        // Arrange
        AttributeRequest request1 = new AttributeRequest(
                null,
                "Color",
                List.of(new AttributeValueRequest(1L, "Red"))
        );

        AttributeRequest request2 = new AttributeRequest(
                null,
                "Color",
                List.of(new AttributeValueRequest(1L, "Red"))
        );

        AttributeRequest request3 = new AttributeRequest(
                1L,
                "Color",
                List.of(new AttributeValueRequest(1L, "Red"))
        );

        // Act & Assert
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
    }

    @Test
    void toString_ReturnsStringWithAllFields() {
        // Arrange
        AttributeRequest request = new AttributeRequest(
                1L,
                "Color",
                List.of(new AttributeValueRequest(1L, "Red"))
        );

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name=Color"));
        assertTrue(toString.contains("values=[AttributeValueRequest[id=1, value=Red]]"));
    }
}