package com.ecommerce.cart;

import com.ecommerce.cartitem.CartItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    private final CartService cartService;
    private final CartItemService cartItemService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCartOrCreateByUserId(@PathVariable Long userId) {
        log.info("Fetching cart for user with ID: {}", userId);
        CartResponse response = cartService.getCartOrCreateByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{userId}/add")
    public ResponseEntity<Void> addItemToCart(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestParam Integer quantity
    ) {
        log.info("Adding product ID: {} with quantity: {} to cart for user ID: {}", productId, quantity, userId);
        cartItemService.addItemToCart(userId, productId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{userId}/remove")
    public ResponseEntity<Void> removeItemFromCart(
            @PathVariable Long userId,
            @RequestParam Long productId
    ) {
        log.info("Removing product ID: {} from cart for user ID: {}", productId, userId);
        cartItemService.removeItemFromCart(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCartByUserId(@PathVariable Long userId) {
        log.info("Clearing cart for user ID: {}", userId);
        cartService.clearCartByUserId(userId);
        return ResponseEntity.noContent().build();
    }
}
