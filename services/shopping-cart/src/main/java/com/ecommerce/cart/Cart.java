package com.ecommerce.cart;

import com.ecommerce.cartitem.CartItem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "cart")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    private Long userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CartItem> cartItems = new HashSet<>();

    /**
     * Adds a cart item to the cart.
     *
     * @param cartItem the cart item to add.
     */
    public void addItem(CartItem cartItem) {
        this.cartItems.add(cartItem);
        cartItem.setCart(this);
    }

    /**
     * Removes a cart item from the cart.
     *
     * @param cartItem the cart item to remove.
     */
    public void removeItem(CartItem cartItem) {
        this.cartItems.remove(cartItem);
        cartItem.setCart(null);
    }

}
