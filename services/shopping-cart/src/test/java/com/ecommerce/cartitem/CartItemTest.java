package com.ecommerce.cartitem;

import com.ecommerce.cart.Cart;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CartItemTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validCartItem_ShouldPassValidation() {
        // Arrange
        Cart cart = new Cart();
        CartItem cartItem = CartItem.builder()
                .productId(1L)
                .quantity(2)
                .cart(cart)
                .build();

        // Act
        Set<ConstraintViolation<CartItem>> violations = validator.validate(cartItem);

        // Assert
        assertTrue(violations.isEmpty(), "Valid cart item should have no validation errors");
    }

    @Test
    void cartItem_NullProductId_ShouldFail() {
        // Arrange
        Cart cart = new Cart();
        CartItem cartItem = CartItem.builder()
                .quantity(2)
                .cart(cart)
                .build();

        // Act
        Set<ConstraintViolation<CartItem>> violations = validator.validate(cartItem);

        // Assert
        assertFalse(violations.isEmpty(), "Cart item with null product ID should fail validation");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("productId")));
    }

    @Test
    void cartItem_QuantityZero_ShouldFail() {
        // Arrange
        Cart cart = new Cart();
        CartItem cartItem = CartItem.builder()
                .productId(1L)
                .quantity(0)
                .cart(cart)
                .build();

        // Act
        Set<ConstraintViolation<CartItem>> violations = validator.validate(cartItem);

        // Assert
        assertFalse(violations.isEmpty(), "Cart item with zero quantity should fail validation");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Kvantita produktu musí být vyšší než 0.")));
    }

    @Test
    void cartItem_NegativeQuantity_ShouldFail() {
        // Arrange
        Cart cart = new Cart();
        CartItem cartItem = CartItem.builder()
                .productId(1L)
                .quantity(-1)
                .cart(cart)
                .build();

        // Act
        Set<ConstraintViolation<CartItem>> violations = validator.validate(cartItem);

        // Assert
        assertFalse(violations.isEmpty(), "Cart item with negative quantity should fail validation");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Kvantita produktu musí být vyšší než 0.")));
    }

    @Test
    void cartItem_EqualsAndHashCode_ShouldWorkCorrectly() {
        // Arrange
        CartItem cartItem1 = new CartItem();
        cartItem1.setId(1L);

        CartItem cartItem2 = new CartItem();
        cartItem2.setId(1L);

        CartItem cartItem3 = new CartItem();
        cartItem3.setId(2L);

        // Act & Assert
        assertEquals(cartItem1, cartItem2);
        assertNotEquals(cartItem1, cartItem3);
        assertEquals(cartItem1.hashCode(), cartItem2.hashCode());
        assertNotEquals(cartItem1.hashCode(), cartItem3.hashCode());
    }

    @Test
    void cartItem_BuilderPattern_ShouldWorkCorrectly() {
        // Arrange
        Cart cart = new Cart();
        Long productId = 1L;
        int quantity = 3;

        // Act
        CartItem cartItem = CartItem.builder()
                .productId(productId)
                .quantity(quantity)
                .cart(cart)
                .build();

        // Assert
        assertEquals(productId, cartItem.getProductId());
        assertEquals(quantity, cartItem.getQuantity());
        assertEquals(cart, cartItem.getCart());
    }

    @Test
    void cartItem_AllArgsConstructor_ShouldWorkCorrectly() {
        // Arrange
        Long id = 1L;
        Long productId = 2L;
        int quantity = 3;
        Cart cart = new Cart();

        // Act
        CartItem cartItem = new CartItem(id, productId, quantity, cart);

        // Assert
        assertEquals(id, cartItem.getId());
        assertEquals(productId, cartItem.getProductId());
        assertEquals(quantity, cartItem.getQuantity());
        assertEquals(cart, cartItem.getCart());
    }
}