package com.ecommerce.cart;

import com.ecommerce.cartitem.CartItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    @Test
    void createCart_WithUserId_ShouldInitializeCorrectly() {
        // Arrange
        Long userId = 1L;

        // Act
        Cart cart = Cart.builder()
                .userId(userId)
                .build();

        // Assert
        assertNotNull(cart);
        assertEquals(userId, cart.getUserId());
        assertNotNull(cart.getCartItems());
        assertTrue(cart.getCartItems().isEmpty());
    }

    @Test
    void addItem_ToCart_ShouldUpdateCartAndCartItem() {
        // Arrange
        Cart cart = new Cart();
        CartItem cartItem = new CartItem();
        cartItem.setProductId(1L);
        cartItem.setQuantity(2);

        // Act
        cart.addItem(cartItem);

        // Assert
        assertTrue(cart.getCartItems().contains(cartItem));
        assertEquals(cart, cartItem.getCart());
        assertEquals(1, cart.getCartItems().size());
    }

    @Test
    void removeItem_FromCart_ShouldUpdateCartAndCartItem() {
        // Arrange
        Cart cart = new Cart();
        CartItem cartItem = new CartItem();
        cartItem.setProductId(1L);
        cartItem.setQuantity(2);
        cart.addItem(cartItem);

        // Act
        cart.removeItem(cartItem);

        // Assert
        assertFalse(cart.getCartItems().contains(cartItem));
        assertNull(cartItem.getCart());
        assertTrue(cart.getCartItems().isEmpty());
    }

    @Test
    void equals_AndHashCode_ShouldWorkCorrectly() {
        // Arrange
        Cart cart1 = new Cart();
        cart1.setId(1L);

        Cart cart2 = new Cart();
        cart2.setId(1L);

        Cart cart3 = new Cart();
        cart3.setId(2L);

        // Act & Assert
        assertEquals(cart1, cart2);
        assertNotEquals(cart1, cart3);
        assertEquals(cart1.hashCode(), cart2.hashCode());
        assertNotEquals(cart1.hashCode(), cart3.hashCode());
    }

    @Test
    void cart_WithMultipleItems_ShouldManageItemsCorrectly() {
        // Arrange
        Cart cart = new Cart();
        CartItem item1 = new CartItem();
        item1.setProductId(1L);
        item1.setQuantity(2);

        CartItem item2 = new CartItem();
        item2.setProductId(2L);
        item2.setQuantity(3);

        // Act
        cart.addItem(item1);
        cart.addItem(item2);

        // Assert
        assertEquals(1, cart.getCartItems().size());
        assertTrue(cart.getCartItems().contains(item1));
        assertTrue(cart.getCartItems().contains(item2));
    }
}