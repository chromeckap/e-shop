package com.ecommerce.payment;

import com.ecommerce.feignclient.user.UserResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PaymentRequestTest {

    private Validator validator;
    private UserResponse user;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        user = new UserResponse(
                1L, "First name", "Second name", "example@email.com"
        );
    }

    @Test
    void validPaymentRequest_ShouldPassValidation() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
                1L,
                100L,
                new BigDecimal("99.99"),
                1L,
                user,
                "2023-01-01T12:00:00"
        );

        // Act
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void paymentRequest_WithNullOrderId_ShouldFailValidation() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
                1L,
                null, // null orderId
                new BigDecimal("99.99"),
                1L,
                user,
                "2023-01-01T12:00:00"
        );

        // Act
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<PaymentRequest> violation = violations.iterator().next();
        assertEquals("ID objednávky nesmí být prázdné.", violation.getMessage());
        assertEquals("orderId", violation.getPropertyPath().toString());
    }

    @Test
    void paymentRequest_WithNullTotalPrice_ShouldFailValidation() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
                1L,
                100L,
                null, // null totalPrice
                1L,
                user,
                "2023-01-01T12:00:00"
        );

        // Act
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<PaymentRequest> violation = violations.iterator().next();
        assertEquals("Částka platby nesmí být prázdná.", violation.getMessage());
        assertEquals("totalPrice", violation.getPropertyPath().toString());
    }

    @Test
    void paymentRequest_WithNegativeTotalPrice_ShouldFailValidation() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
                1L,
                100L,
                new BigDecimal("-10.00"), // negative totalPrice
                1L,
                user,
                "2023-01-01T12:00:00"
        );

        // Act
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<PaymentRequest> violation = violations.iterator().next();
        assertEquals("Částka platby musí být kladná.", violation.getMessage());
        assertEquals("totalPrice", violation.getPropertyPath().toString());
    }

    @Test
    void paymentRequest_WithZeroTotalPrice_ShouldFailValidation() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
                1L,
                100L,
                BigDecimal.ZERO, // zero totalPrice
                1L,
                user,
                "2023-01-01T12:00:00"
        );

        // Act
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<PaymentRequest> violation = violations.iterator().next();
        assertEquals("Částka platby musí být kladná.", violation.getMessage());
        assertEquals("totalPrice", violation.getPropertyPath().toString());
    }

    @Test
    void paymentRequest_WithNullPaymentMethodId_ShouldFailValidation() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
                1L,
                100L,
                new BigDecimal("99.99"),
                null, // null paymentMethodId
                user,
                "2023-01-01T12:00:00"
        );

        // Act
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<PaymentRequest> violation = violations.iterator().next();
        assertEquals("Typ platební metody nesmí být prázdný.", violation.getMessage());
        assertEquals("paymentMethodId", violation.getPropertyPath().toString());
    }

    @Test
    void paymentRequest_WithNullUser_ShouldFailValidation() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
                1L,
                100L,
                new BigDecimal("99.99"),
                1L,
                null, // null user
                "2023-01-01T12:00:00"
        );

        // Act
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<PaymentRequest> violation = violations.iterator().next();
        assertEquals("Odpověď uživatele nesmí být prázdná.", violation.getMessage());
        assertEquals("user", violation.getPropertyPath().toString());
    }

    @Test
    void paymentRequest_WithMultipleValidationErrors_ShouldReportAllErrors() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
                1L,
                null, // null orderId
                new BigDecimal("-10.00"), // negative totalPrice
                null, // null paymentMethodId
                null, // null user
                "2023-01-01T12:00:00"
        );

        // Act
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(4, violations.size()); // All four required fields have validation errors
    }

    @Test
    void paymentRequest_ShouldBeImmutable() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
                1L,
                100L,
                new BigDecimal("99.99"),
                1L,
                user,
                "2023-01-01T12:00:00"
        );

        // Act & Assert - Verify record accessors return the same values that were passed in
        assertEquals(1L, request.id());
        assertEquals(100L, request.orderId());
        assertEquals(new BigDecimal("99.99"), request.totalPrice());
        assertEquals(1L, request.paymentMethodId());
        assertEquals(user, request.user());
        assertEquals("2023-01-01T12:00:00", request.OrderCreateTime());
    }

    @Test
    void paymentRequest_WithSameValues_ShouldBeEqual() {
        // Arrange
        PaymentRequest request1 = new PaymentRequest(
                1L,
                100L,
                new BigDecimal("99.99"),
                1L,
                user,
                "2023-01-01T12:00:00"
        );

        PaymentRequest request2 = new PaymentRequest(
                1L,
                100L,
                new BigDecimal("99.99"),
                1L,
                user,
                "2023-01-01T12:00:00"
        );

        // Act & Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void paymentRequest_WithDifferentValues_ShouldNotBeEqual() {
        // Arrange
        PaymentRequest request1 = new PaymentRequest(
                1L,
                100L,
                new BigDecimal("99.99"),
                1L,
                user,
                "2023-01-01T12:00:00"
        );

        PaymentRequest request2 = new PaymentRequest(
                2L, // Different ID
                100L,
                new BigDecimal("99.99"),
                1L,
                user,
                "2023-01-01T12:00:00"
        );

        // Act & Assert
        assertNotEquals(request1, request2);
        assertNotEquals(request1.hashCode(), request2.hashCode());
    }
}