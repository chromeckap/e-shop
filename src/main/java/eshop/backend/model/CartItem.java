package eshop.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart_item")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1, message = "Quantity must be at least 1.")
    private Integer quantity = 1;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "variant_id")
    private Variant variant;

    public CartItem(Cart cart, Variant variant) {
        this.cart = cart;
        this.variant = variant;
    }

    public BigDecimal getTotalPrice() {
        return variant.getDiscountedPrice()
                .multiply(BigDecimal.valueOf(quantity));
    }
}