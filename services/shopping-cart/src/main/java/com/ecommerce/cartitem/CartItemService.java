package com.ecommerce.cartitem;

import com.ecommerce.cart.Cart;
import com.ecommerce.cart.CartService;
import com.ecommerce.exception.CartItemNotFoundException;
import com.ecommerce.security.SecurityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartItemValidator cartItemValidator;
    private final CartService cartService;
    private final SecurityValidator securityValidator;

    /**
     * Adds an item to a user's cart. If the item already exists in the cart, its quantity is incremented.
     *
     * @param userId    the id of the user (must not be null)
     * @param productId the id of the product to add (must not be null)
     * @param quantity  the quantity to add (must be positive)
     */
    @Transactional
    public void addItemToCart(Long userId, Long productId, Integer quantity) {
        Objects.requireNonNull(userId, "ID uživatele nesmí být prázdné.");
        Objects.requireNonNull(productId, "ID produktu nesmí být prázdné.");
        Objects.requireNonNull(quantity, "Kvantita nesmí být prázdná.");
        log.debug("Adding product {} with quantity {} to cart for user {}", productId, quantity, userId);

        securityValidator.validateUserAccess(userId);

        Cart cart = cartService.findCartOrCreateByUserId(userId);

        CartItem cartItem = cartItemRepository.findByCartAndProductId(cart, productId)
                        .orElseGet(() -> CartItem.builder()
                                .productId(productId)
                                .cart(cart)
                                .quantity(0)
                                .build());

        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartItemValidator.validateQuantityIsGreaterThanZero(cartItem.getQuantity());
        cart.addItem(cartItem);
        cartItemRepository.save(cartItem);
    }

    /**
     * Removes an item from a user's cart.
     *
     * @param userId    the id of the user (must not be null)
     * @param productId the id of the product to remove (must not be null)
     * @throws CartItemNotFoundException if the product is not found in the user's cart
     */
    @Transactional
    public void removeItemFromCart(Long userId, Long productId) {
        Objects.requireNonNull(userId, "ID uživatele nesmí být prázdné.");
        Objects.requireNonNull(productId, "ID produktu nesmí být prázdné.");
        log.debug("Removing product {} from cart for user {}", productId, userId);

        securityValidator.validateUserAccess(userId);

        Cart cart = cartService.findCartOrCreateByUserId(userId);
        Optional<CartItem> cartItem = cartItemRepository.findByCartAndProductId(cart, productId);

        cartItemRepository.delete(
                cartItem.orElseThrow(() -> new CartItemNotFoundException(
                        String.format("Produkt s ID %s nebyl nalezen v košíku uživatele s ID %s.", productId, userId)
                )));
        log.info("Removed product {} from cart for user {}", productId, userId);
    }

}
