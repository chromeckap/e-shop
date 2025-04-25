package com.ecommerce.cartitem;

import com.ecommerce.cart.Cart;
import com.ecommerce.cart.CartService;
import com.ecommerce.exception.CartItemNotFoundException;
import com.ecommerce.exception.UnauthorizedAccessException;
import com.ecommerce.security.SecurityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartItemValidator cartItemValidator;

    @Mock
    private CartService cartService;

    @Mock
    private SecurityValidator securityValidator;

    private CartItemService cartItemService;

    @BeforeEach
    void setUp() {
        cartItemService = new CartItemService(
                cartItemRepository,
                cartItemValidator,
                cartService,
                securityValidator
        );
    }

    @Test
    void addItemToCart_NewItem_AddsToCart() {
        // Arrange
        Long userId = 1L;
        Long productId = 2L;
        Integer quantity = 3;

        Cart cart = new Cart();
        cart.setUserId(userId);

        // Mock method calls
        doNothing().when(securityValidator).validateUserAccess(userId);
        when(cartService.findCartOrCreateByUserId(userId)).thenReturn(cart);
        when(cartItemRepository.findByCartAndProductId(cart, productId))
                .thenReturn(Optional.empty());
        doNothing().when(cartItemValidator).validateQuantityIsGreaterThanZero(quantity);
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        cartItemService.addItemToCart(userId, productId, quantity);

        // Assert
        verify(securityValidator).validateUserAccess(userId);
        verify(cartService).findCartOrCreateByUserId(userId);
        verify(cartItemRepository).findByCartAndProductId(cart, productId);
        verify(cartItemValidator).validateQuantityIsGreaterThanZero(quantity);
        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    void addItemToCart_ExistingItem_IncreasesQuantity() {
        // Arrange
        Long userId = 1L;
        Long productId = 2L;
        Integer quantity = 3;

        Cart cart = new Cart();
        cart.setUserId(userId);

        CartItem existingCartItem = new CartItem();
        existingCartItem.setProductId(productId);
        existingCartItem.setQuantity(2);
        existingCartItem.setCart(cart);

        // Mock method calls
        doNothing().when(securityValidator).validateUserAccess(userId);
        when(cartService.findCartOrCreateByUserId(userId)).thenReturn(cart);
        when(cartItemRepository.findByCartAndProductId(cart, productId))
                .thenReturn(Optional.of(existingCartItem));
        doNothing().when(cartItemValidator).validateQuantityIsGreaterThanZero(5);
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        cartItemService.addItemToCart(userId, productId, quantity);

        // Assert
        assertEquals(5, existingCartItem.getQuantity());
        verify(securityValidator).validateUserAccess(userId);
        verify(cartService).findCartOrCreateByUserId(userId);
        verify(cartItemRepository).findByCartAndProductId(cart, productId);
        verify(cartItemValidator).validateQuantityIsGreaterThanZero(5);
        verify(cartItemRepository).save(existingCartItem);
    }

    @Test
    void addItemToCart_UnauthorizedUser_ThrowsException() {
        // Arrange
        Long userId = 1L;
        Long productId = 2L;
        Integer quantity = 3;

        // Mock method calls
        doThrow(new UnauthorizedAccessException("Unauthorized"))
                .when(securityValidator).validateUserAccess(userId);

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class,
                () -> cartItemService.addItemToCart(userId, productId, quantity),
                "Should throw UnauthorizedAccessException for unauthorized user"
        );
    }

    @Test
    void removeItemFromCart_ExistingItem_RemovesSuccessfully() {
        // Arrange
        Long userId = 1L;
        Long productId = 2L;

        Cart cart = new Cart();
        cart.setUserId(userId);

        CartItem cartItem = new CartItem();
        cartItem.setProductId(productId);
        cartItem.setCart(cart);

        // Mock method calls
        doNothing().when(securityValidator).validateUserAccess(userId);
        when(cartService.findCartOrCreateByUserId(userId)).thenReturn(cart);
        when(cartItemRepository.findByCartAndProductId(cart, productId))
                .thenReturn(Optional.of(cartItem));
        doNothing().when(cartItemRepository).delete(cartItem);

        // Act
        cartItemService.removeItemFromCart(userId, productId);

        // Assert
        verify(securityValidator).validateUserAccess(userId);
        verify(cartService).findCartOrCreateByUserId(userId);
        verify(cartItemRepository).findByCartAndProductId(cart, productId);
        verify(cartItemRepository).delete(cartItem);
    }

    @Test
    void removeItemFromCart_NonExistingItem_ThrowsCartItemNotFoundException() {
        // Arrange
        Long userId = 1L;
        Long productId = 2L;

        Cart cart = new Cart();
        cart.setUserId(userId);

        // Mock method calls
        doNothing().when(securityValidator).validateUserAccess(userId);
        when(cartService.findCartOrCreateByUserId(userId)).thenReturn(cart);
        when(cartItemRepository.findByCartAndProductId(cart, productId))
                .thenReturn(Optional.empty());

        // Act & Assert
        CartItemNotFoundException exception = assertThrows(
                CartItemNotFoundException.class,
                () -> cartItemService.removeItemFromCart(userId, productId),
                "Should throw CartItemNotFoundException for non-existing item"
        );

        assertEquals(
                String.format("Produkt s ID %s nebyl nalezen v košíku uživatele s ID %s.", productId, userId),
                exception.getMessage()
        );

        verify(securityValidator).validateUserAccess(userId);
        verify(cartService).findCartOrCreateByUserId(userId);
        verify(cartItemRepository).findByCartAndProductId(cart, productId);
    }

    @Test
    void addItemToCart_NullParameters_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> cartItemService.addItemToCart(null, 2L, 3),
                "Should throw NullPointerException for null userId"
        );

        assertThrows(NullPointerException.class,
                () -> cartItemService.addItemToCart(1L, null, 3),
                "Should throw NullPointerException for null productId"
        );

        assertThrows(NullPointerException.class,
                () -> cartItemService.addItemToCart(1L, 2L, null),
                "Should throw NullPointerException for null quantity"
        );
    }

    @Test
    void removeItemFromCart_NullParameters_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> cartItemService.removeItemFromCart(null, 2L),
                "Should throw NullPointerException for null userId"
        );

        assertThrows(NullPointerException.class,
                () -> cartItemService.removeItemFromCart(1L, null),
                "Should throw NullPointerException for null productId"
        );
    }
}