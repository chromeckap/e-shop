package com.ecommerce.cart;

import com.ecommerce.cartitem.CartItem;
import com.ecommerce.cartitem.CartItemRequest;
import com.ecommerce.feignclient.product.ProductClient;
import com.ecommerce.feignclient.product.PurchaseResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartMapperTest {

    @Mock
    private ProductClient productClient;

    private CartMapper cartMapper;

    @BeforeEach
    void setUp() {
        cartMapper = new CartMapper(productClient);
    }

    @Test
    void toCart_ValidUserId_CreatesCartCorrectly() {
        // Arrange
        Long userId = 1L;

        // Act
        Cart cart = cartMapper.toCart(userId);

        // Assert
        assertNotNull(cart);
        assertEquals(userId, cart.getUserId());
        assertNotNull(cart.getCartItems());
        assertTrue(cart.getCartItems().isEmpty());
    }

    @Test
    void toResponse_EmptyCart_CalculatesTotalPriceZero() {
        // Arrange
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUserId(1L);

        // Mock product client to return empty list
        when(productClient.getVariantsByCartItems(anyList()))
                .thenReturn(List.of());

        // Act
        CartResponse response = cartMapper.toResponse(cart);

        // Assert
        assertNotNull(response);
        assertEquals(cart.getId(), response.id());
        assertEquals(cart.getUserId(), response.userId());
        assertEquals(BigDecimal.ZERO, response.totalPrice());
        assertTrue(response.items().isEmpty());
    }

    @Test
    void toResponse_CartWithItems_CalculatesTotalPriceCorrectly() {
        // Arrange
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUserId(1L);

        // Create cart items
        CartItem item1 = new CartItem();
        item1.setProductId(1L);
        item1.setQuantity(2);

        CartItem item2 = new CartItem();
        item2.setProductId(2L);
        item2.setQuantity(3);

        cart.addItem(item1);
        cart.addItem(item2);

        // Prepare mock purchase responses
        List<PurchaseResponse> purchaseResponses = Arrays.asList(
                new PurchaseResponse(1L, 1L, "Product 1", "/", BigDecimal.valueOf(10), 2, 3, true, BigDecimal.valueOf(20), null),
                new PurchaseResponse(2L, 2L, "Product 2", "/", BigDecimal.valueOf(10), 2, 3, true, BigDecimal.valueOf(20), null)
        );

        // Mock product client
        when(productClient.getVariantsByCartItems(anyList()))
                .thenReturn(purchaseResponses);

        // Act
        CartResponse response = cartMapper.toResponse(cart);

        // Assert
        assertNotNull(response);
        assertEquals(cart.getId(), response.id());
        assertEquals(cart.getUserId(), response.userId());

        // Check total price calculation
        BigDecimal expectedTotalPrice = BigDecimal.valueOf(20).add(BigDecimal.valueOf(20));
        assertEquals(expectedTotalPrice, response.totalPrice());

        // Check items
        assertEquals(2, response.items().size());

        // Verify interactions
        verify(productClient).getVariantsByCartItems(anyList());
    }

    @Test
    void toResponse_NullCart_ThrowsException() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> cartMapper.toResponse(null),
                "Should throw NullPointerException for null cart"
        );
    }
}