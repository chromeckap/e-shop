package com.ecommerce.cart;

import com.ecommerce.security.SecurityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final SecurityValidator securityValidator;

    /**
     * Finds a cart for the given userId. If none exists, a new cart is created.
     *
     * @param userId the id of the user (must not be null)
     * @return the existing or newly created Cart
     */
    @Transactional(readOnly = true)
    public Cart findCartOrCreateByUserId(Long userId) {
        Objects.requireNonNull(userId, "ID uživatele nesmí být prázdné.");
        log.debug("Looking for cart with userId: {}", userId);

        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    //var user = userClient.getUserById(userId);
                    Cart newCart = cartMapper.toCart(userId);
                    Cart savedCart = cartRepository.save(newCart);

                    log.info("New cart created for userId: {} with cartId: {}", userId, savedCart.getId());
                    return savedCart;
                });
    }

    /**
     * Retrieves a cart for the specified user id and maps it to a CartResponse.
     *
     * @param userId the id of the user (must not be null)
     * @return the CartResponse DTO representing the user's cart
     */
    @Transactional
    public CartResponse getCartOrCreateByUserId(Long userId) {
        Objects.requireNonNull(userId, "ID uživatele nesmí být prázdné.");
        log.debug("Fetching cart for userId: {}", userId);

        securityValidator.validateUserAccess(userId);

        Cart cart = this.findCartOrCreateByUserId(userId);
        return cartMapper.toResponse(cart);
    }

    /**
     * Clears all items in the cart for the specified user.
     *
     * @param userId the id of the user (must not be null)
     */
    @Transactional
    public void clearCartByUserId(Long userId) {
        Objects.requireNonNull(userId, "ID uživatele nesmí být prázdné.");
        log.debug("Clearing cart for userId: {}", userId);

        Cart cart = this.findCartOrCreateByUserId(userId);
        cart.getCartItems().clear();

        cartRepository.save(cart);
        log.info("Cart cleared for userId: {}", userId);
    }
}
