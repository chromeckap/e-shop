package com.ecommerce.cart;

import com.ecommerce.cartitem.CartItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private CartItemService cartItemService;

    private CartController cartController;

    @BeforeEach
    void setUp() {
        cartController = new CartController(cartService, cartItemService);
    }

    @Test
    void getCartOrCreateByUserId_ExistingCart_ReturnsCartResponse() {
        // Arrange
        Long userId = 1L;
        CartResponse mockCartResponse = CartResponse.builder()
                .id(1L)
                .userId(userId)
                .totalPrice(BigDecimal.TEN)
                .items(Collections.emptyList())
                .build();

        when(cartService.getCartOrCreateByUserId(userId))
                .thenReturn(mockCartResponse);

        // Act
        ResponseEntity<CartResponse> response = cartController.getCartOrCreateByUserId(userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockCartResponse, response.getBody());
        verify(cartService).getCartOrCreateByUserId(userId);
    }

    @Test
    void addItemToCart_ValidRequest_ReturnsCreatedStatus() {
        // Arrange
        Long userId = 1L;
        Long productId = 2L;
        Integer quantity = 3;

        doNothing().when(cartItemService).addItemToCart(userId, productId, quantity);

        // Act
        ResponseEntity<Void> response = cartController.addItemToCart(userId, productId, quantity);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(cartItemService).addItemToCart(userId, productId, quantity);
    }

    @Test
    void removeItemFromCart_ValidRequest_ReturnsNoContent() {
        // Arrange
        Long userId = 1L;
        Long productId = 2L;

        doNothing().when(cartItemService).removeItemFromCart(userId, productId);

        // Act
        ResponseEntity<Void> response = cartController.removeItemFromCart(userId, productId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cartItemService).removeItemFromCart(userId, productId);
    }

    @Test
    void clearCartByUserId_ValidRequest_ReturnsNoContent() {
        // Arrange
        Long userId = 1L;

        doNothing().when(cartService).clearCartByUserId(userId);

        // Act
        ResponseEntity<Void> response = cartController.clearCartByUserId(userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cartService).clearCartByUserId(userId);
    }
}