package com.ecommerce.paymentmethod;

import com.ecommerce.strategy.PaymentGatewayType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PaymentMethodRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validPaymentMethodRequest_ShouldPassValidation() {
        // Arrange
        PaymentMethodRequest request = new PaymentMethodRequest(
                1L,
                "Credit Card",
                PaymentGatewayType.STRIPE_CARD,
                true,
                new BigDecimal("5.99"),
                true,
                new BigDecimal("100.00")
        );

        // Act
        Set<ConstraintViolation<PaymentMethodRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void paymentMethodRequest_WithNullName_ShouldFailValidation() {
        // Arrange
        PaymentMethodRequest request = new PaymentMethodRequest(
                1L,
                null, // null name
                PaymentGatewayType.STRIPE_CARD,
                true,
                new BigDecimal("5.99"),
                true,
                new BigDecimal("100.00")
        );

        // Act
        Set<ConstraintViolation<PaymentMethodRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());

        ConstraintViolation<PaymentMethodRequest> violation = violations.iterator().next();
        assertEquals("Název platební metody nesmí být prázdný.", violation.getMessage());
    }

    @Test
    void paymentMethodRequest_WithEmptyName_ShouldFailValidation() {
        // Arrange
        PaymentMethodRequest request = new PaymentMethodRequest(
                1L,
                "", // empty name
                PaymentGatewayType.STRIPE_CARD,
                true,
                new BigDecimal("5.99"),
                true,
                new BigDecimal("100.00")
        );

        // Act
        Set<ConstraintViolation<PaymentMethodRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());

        ConstraintViolation<PaymentMethodRequest> violation = violations.iterator().next();
        assertEquals("Název platební metody nesmí být prázdný.", violation.getMessage());
    }

    @Test
    void paymentMethodRequest_WithNullType_ShouldFailValidation() {
        // Arrange
        PaymentMethodRequest request = new PaymentMethodRequest(
                1L,
                "Credit Card",
                null, // null type
                true,
                new BigDecimal("5.99"),
                true,
                new BigDecimal("100.00")
        );

        // Act
        Set<ConstraintViolation<PaymentMethodRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<PaymentMethodRequest> violation = violations.iterator().next();
        assertEquals("Typ nesmí být prázdný.", violation.getMessage());
    }

    @Test
    void paymentMethodRequest_WithNullPrice_ShouldFailValidation() {
        // Arrange
        PaymentMethodRequest request = new PaymentMethodRequest(
                1L,
                "Credit Card",
                PaymentGatewayType.STRIPE_CARD,
                true,
                null, // null price
                true,
                new BigDecimal("100.00")
        );

        // Act
        Set<ConstraintViolation<PaymentMethodRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<PaymentMethodRequest> violation = violations.iterator().next();
        assertEquals("Cena nesmí být prázdná.", violation.getMessage());
    }

    @Test
    void paymentMethodRequest_WithNegativePrice_ShouldFailValidation() {
        // Arrange
        PaymentMethodRequest request = new PaymentMethodRequest(
                1L,
                "Credit Card",
                PaymentGatewayType.STRIPE_CARD,
                true,
                new BigDecimal("-5.99"), // negative price
                true,
                new BigDecimal("100.00")
        );

        // Act
        Set<ConstraintViolation<PaymentMethodRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<PaymentMethodRequest> violation = violations.iterator().next();
        assertEquals("Cena musí být kladná nebo nulová.", violation.getMessage());
    }

    @Test
    void paymentMethodRequest_WithNegativeFreeForOrderAbove_ShouldFailValidation() {
        // Arrange
        PaymentMethodRequest request = new PaymentMethodRequest(
                1L,
                "Credit Card",
                PaymentGatewayType.STRIPE_CARD,
                true,
                new BigDecimal("5.99"),
                true,
                new BigDecimal("-100.00") // negative freeForOrderAbove
        );

        // Act
        Set<ConstraintViolation<PaymentMethodRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<PaymentMethodRequest> violation = violations.iterator().next();
        assertEquals("Limit pro platební metodu musí být kladný nebo nulový.", violation.getMessage());
    }

    @Test
    void paymentMethodRequest_WithNullFreeForOrderAbove_WhenIsFreeForOrderAboveTrue_ShouldPassValidation() {
        // Arrange - Note: This should be valid since the freeForOrderAbove can be null
        PaymentMethodRequest request = new PaymentMethodRequest(
                1L,
                "Credit Card",
                PaymentGatewayType.STRIPE_CARD,
                true,
                new BigDecimal("5.99"),
                true,
                null // null freeForOrderAbove
        );

        // Act
        Set<ConstraintViolation<PaymentMethodRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void paymentMethodRequest_WithMultipleValidationErrors_ShouldReportAllErrors() {
        // Arrange
        PaymentMethodRequest request = new PaymentMethodRequest(
                1L,
                "", // empty name
                null, // null type
                true,
                new BigDecimal("-5.99"), // negative price
                true,
                new BigDecimal("-100.00") // negative freeForOrderAbove
        );

        // Act
        Set<ConstraintViolation<PaymentMethodRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(5, violations.size());
    }

    @Test
    void paymentMethodRequest_AccessorsShouldReturnCorrectValues() {
        // Arrange
        Long id = 1L;
        String name = "Credit Card";
        PaymentGatewayType type = PaymentGatewayType.STRIPE_CARD;
        boolean isActive = true;
        BigDecimal price = new BigDecimal("5.99");
        boolean isFreeForOrderAbove = true;
        BigDecimal freeForOrderAbove = new BigDecimal("100.00");

        // Act
        PaymentMethodRequest request = new PaymentMethodRequest(
                id, name, type, isActive, price, isFreeForOrderAbove, freeForOrderAbove
        );

        // Assert
        assertEquals(id, request.id());
        assertEquals(name, request.name());
        assertEquals(type, request.type());
        assertEquals(isActive, request.isActive());
        assertEquals(price, request.price());
        assertEquals(isFreeForOrderAbove, request.isFreeForOrderAbove());
        assertEquals(freeForOrderAbove, request.freeForOrderAbove());
    }

    @Test
    void paymentMethodRequest_WithSameValues_ShouldBeEqual() {
        // Arrange
        PaymentMethodRequest request1 = new PaymentMethodRequest(
                1L,
                "Credit Card",
                PaymentGatewayType.STRIPE_CARD,
                true,
                new BigDecimal("5.99"),
                true,
                new BigDecimal("100.00")
        );

        // Act & Assert
        assertEquals(request1, request1);
        assertEquals(request1.hashCode(), request1.hashCode());
    }

    @Test
    void paymentMethodRequest_WithDifferentValues_ShouldNotBeEqual() {
        // Arrange
        PaymentMethodRequest request1 = new PaymentMethodRequest(
                1L,
                "Credit Card",
                PaymentGatewayType.STRIPE_CARD,
                true,
                new BigDecimal("5.99"),
                true,
                new BigDecimal("100.00")
        );

        PaymentMethodRequest request2 = new PaymentMethodRequest(
                2L, // Different ID
                "Credit Card",
                PaymentGatewayType.STRIPE_CARD,
                true,
                new BigDecimal("5.99"),
                true,
                new BigDecimal("100.00")
        );

        // Act & Assert
        assertNotEquals(request1, request2);
    }
}