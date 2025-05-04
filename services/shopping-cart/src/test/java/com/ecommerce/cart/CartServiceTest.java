package com.ecommerce.cart;

import com.ecommerce.cartitem.CartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartMapper cartMapper;

    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartService(cartRepository, cartMapper, null);
    }

    @Test
    void findCartOrCreateByUserId_ExistingCart_ReturnsCart() {
        // Arrange
        Long userId = 1L;
        Cart existingCart = new Cart();
        existingCart.setId(1L);
        existingCart.setUserId(userId);

        when(cartRepository.findByUserId(userId))
                .thenReturn(Optional.of(existingCart));

        // Act
        Cart foundCart = cartService.findCartOrCreateByUserId(userId);

        // Assert
        assertNotNull(foundCart);
        assertEquals(existingCart, foundCart);
        verify(cartRepository).findByUserId(userId);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void findCartOrCreateByUserId_NewCart_CreatesAndReturnsCart() {
        // Arrange
        Long userId = 1L;
        Cart newCart = new Cart();
        newCart.setId(1L);
        newCart.setUserId(userId);

        when(cartRepository.findByUserId(userId))
                .thenReturn(Optional.empty());
        when(cartMapper.toCart(userId))
                .thenReturn(newCart);
        when(cartRepository.save(newCart))
                .thenReturn(newCart);

        // Act
        Cart foundCart = cartService.findCartOrCreateByUserId(userId);

        // Assert
        assertNotNull(foundCart);
        assertEquals(newCart, foundCart);
        verify(cartRepository).findByUserId(userId);
        verify(cartMapper).toCart(userId);
        verify(cartRepository).save(newCart);
    }

    @Test
    void findCartOrCreateByUserId_NullUserId_ThrowsException() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> cartService.findCartOrCreateByUserId(null),
                "Should throw NullPointerException for null userId"
        );
    }

    @Test
    void clearCartByUserId_ClearsCartItems() {
        // Arrange
        Long userId = 1L;
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUserId(userId);

        // Add some dummy cart items
        CartItem item1 = new CartItem();
        CartItem item2 = new CartItem();
        cart.addItem(item1);
        cart.addItem(item2);

        when(cartRepository.findByUserId(userId))
                .thenReturn(Optional.of(cart));
        when(cartRepository.save(cart))
                .thenReturn(cart);

        // Act
        cartService.clearCartByUserId(userId);

        // Assert
        assertTrue(cart.getCartItems().isEmpty());
        verify(cartRepository).save(cart);
    }

    @Test
    void clearCartByUserId_NullUserId_ThrowsException() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> cartService.clearCartByUserId(null),
                "Should throw NullPointerException for null userId"
        );
    }
}