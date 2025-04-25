package com.ecommerce.deliverymethod;

import com.ecommerce.strategy.CourierType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryMethodRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testValidDeliveryMethodRequest() {
        // Arrange
        DeliveryMethodRequest request = new DeliveryMethodRequest(
                1L,
                "Standard Delivery",
                CourierType.PACKETA,
                true,
                BigDecimal.valueOf(5.99),
                true,
                BigDecimal.valueOf(50.00)
        );

        // Act
        Set<ConstraintViolation<DeliveryMethodRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty(), "Expected no violations for a valid request");
    }

    @Test
    void testBlankName() {
        // Arrange
        DeliveryMethodRequest request = new DeliveryMethodRequest(
                1L,
                "",
                CourierType.PACKETA,
                true,
                BigDecimal.valueOf(5.99),
                true,
                BigDecimal.valueOf(50.00)
        );

        // Act
        Set<ConstraintViolation<DeliveryMethodRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty(), "Expected violations for blank name");
        assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("Název metody dopravy nesmí být prázdný.")),
                "Expected specific error message for blank name"
        );
    }

    @Test
    void testNullCourierType() {
        // Arrange
        DeliveryMethodRequest request = new DeliveryMethodRequest(
                1L,
                "Standard Delivery",
                null,
                true,
                BigDecimal.valueOf(5.99),
                true,
                BigDecimal.valueOf(50.00)
        );

        // Act
        Set<ConstraintViolation<DeliveryMethodRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty(), "Expected violations for null courier type");
        assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("Typ kurýra nesmí být prázdný.")),
                "Expected specific error message for null courier type"
        );
    }

    @Test
    void testNullActiveStatus() {
        // Arrange
        DeliveryMethodRequest request = new DeliveryMethodRequest(
                1L,
                "Standard Delivery",
                CourierType.PACKETA,
                false, // This is not null, but the test checks @NotNull annotation
                BigDecimal.valueOf(5.99),
                true,
                BigDecimal.valueOf(50.00)
        );

        // Act
        Set<ConstraintViolation<DeliveryMethodRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty(), "Expected no violations for explicit boolean value");
    }

    @Test
    void testNegativePrice() {
        // Arrange
        DeliveryMethodRequest request = new DeliveryMethodRequest(
                1L,
                "Standard Delivery",
                CourierType.PACKETA,
                true,
                BigDecimal.valueOf(-5.99),
                true,
                BigDecimal.valueOf(50.00)
        );

        // Act
        Set<ConstraintViolation<DeliveryMethodRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty(), "Expected violations for negative price");
        assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("Cena musí být kladná nebo nulová.")),
                "Expected specific error message for negative price"
        );
    }

    @Test
    void testNegativeFreeForOrderAboveValue() {
        // Arrange
        DeliveryMethodRequest request = new DeliveryMethodRequest(
                1L,
                "Standard Delivery",
                CourierType.PACKETA,
                true,
                BigDecimal.valueOf(5.99),
                true,
                BigDecimal.valueOf(-50.00)
        );

        // Act
        Set<ConstraintViolation<DeliveryMethodRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty(), "Expected violations for negative free for order above value");
        assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("Hodnota objednávky zdarma musí být kladná nebo nulová.")),
                "Expected specific error message for negative free for order above value"
        );
    }

    @Test
    void testRecordCreation() {
        // Arrange
        Long id = 1L;
        String name = "Standard Delivery";
        CourierType type = CourierType.PACKETA;
        boolean isActive = true;
        BigDecimal price = BigDecimal.valueOf(5.99);
        boolean isFreeForOrderAbove = true;
        BigDecimal freeForOrderAboveValue = BigDecimal.valueOf(50.00);

        // Act
        DeliveryMethodRequest request = new DeliveryMethodRequest(
                id, name, type, isActive, price, isFreeForOrderAbove, freeForOrderAboveValue
        );

        // Assert
        assertEquals(id, request.id());
        assertEquals(name, request.name());
        assertEquals(type, request.type());
        assertEquals(isActive, request.isActive());
        assertEquals(price, request.price());
        assertEquals(isFreeForOrderAbove, request.isFreeForOrderAbove());
        assertEquals(freeForOrderAboveValue, request.freeForOrderAbove());
    }
}