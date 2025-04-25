package com.ecommerce.cartitem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartItemValidatorTest {

    private CartItemValidator cartItemValidator;

    @BeforeEach
    void setUp() {
        cartItemValidator = new CartItemValidator();
    }

    @Test
    void validateQuantityIsGreaterThanZero_PositiveQuantity_ShouldPass() {
        // Arrange
        Integer quantity = 1;

        // Act & Assert
        assertDoesNotThrow(() -> cartItemValidator.validateQuantityIsGreaterThanZero(quantity),
                "Positive quantity should pass validation");
    }

    @Test
    void validateQuantityIsGreaterThanZero_ZeroQuantity_ShouldPass() {
        // Arrange
        Integer quantity = 0;

        // Act & Assert
        assertDoesNotThrow(() -> cartItemValidator.validateQuantityIsGreaterThanZero(quantity),
                "Zero quantity should pass validation");
    }

    @Test
    void validateQuantityIsGreaterThanZero_NegativeQuantity_ShouldThrowIllegalArgumentException() {
        // Arrange
        Integer quantity = -1;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartItemValidator.validateQuantityIsGreaterThanZero(quantity),
                "Negative quantity should throw IllegalArgumentException"
        );

        assertEquals(
                "Kvantita musí být vyšší než 0.",
                exception.getMessage(),
                "Exception message should match expected text"
        );
    }

    @Test
    void validateQuantityIsGreaterThanZero_NullQuantity_ShouldThrowNullPointerException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> cartItemValidator.validateQuantityIsGreaterThanZero(null),
                "Null quantity should throw NullPointerException"
        );
    }

    @Test
    void validateQuantityIsGreaterThanZero_LargePositiveQuantity_ShouldPass() {
        // Arrange
        Integer quantity = Integer.MAX_VALUE;

        // Act & Assert
        assertDoesNotThrow(() -> cartItemValidator.validateQuantityIsGreaterThanZero(quantity),
                "Large positive quantity should pass validation");
    }
}