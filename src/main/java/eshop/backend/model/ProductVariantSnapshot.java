package eshop.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product_variant_snapshot")
public class ProductVariantSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private Long variantId;

    private String name;

    private String description;

    private BigDecimal price;

    @OneToMany(mappedBy = "productVariantSnapshot")
    private Set<OrderItem> orderItems;


    public ProductVariantSnapshot(CartItem cartItem) {
        Product product = cartItem.getVariant().getProduct();
        Variant variant = cartItem.getVariant();

        this.productId = product.getId();
        this.variantId = variant.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = variant.getBasePrice();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductVariantSnapshot that = (ProductVariantSnapshot) o;

        return Objects.equals(productId, that.productId)
                && Objects.equals(variantId, that.variantId)
                && Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(price, that.price);
    }

}