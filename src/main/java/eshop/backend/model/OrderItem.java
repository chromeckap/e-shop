package eshop.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1, message = "Quantity must be at least 1.")
    private Integer quantity;

    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_variant_snapshot_id")
    private ProductVariantSnapshot productVariantSnapshot;

    public OrderItem(CartItem cartItem, ProductVariantSnapshot snapshot) {
        this.quantity = cartItem.getQuantity();
        this.productVariantSnapshot = snapshot;
    }

    public BigDecimal totalPrice() {
        return productVariantSnapshot.getPrice()
                .multiply(BigDecimal.valueOf(quantity));
    }

}
