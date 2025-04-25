package com.ecommerce.cartitem;

import com.ecommerce.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Finds a cart item by its associated cart and product ID.
     *
     * @param cart the cart in which the item is located.
     * @param productId the ID of the product to search for in the cart.
     * @return an Optional containing the CartItem if found, or an empty Optional if not found.
     */
    Optional<CartItem> findByCartAndProductId(Cart cart, Long productId);

}
