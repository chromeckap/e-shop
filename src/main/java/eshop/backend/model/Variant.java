package eshop.backend.model;

import eshop.backend.request.VariantRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "variant")
public class Variant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 0, message = "Price must be at least 0.")
    private BigDecimal basePrice;
    private BigDecimal discountedPrice;

    private String sku;
    private boolean unlimitedQuantity; //todo check

    @OneToMany(mappedBy = "variant")
    private Set<Inventory> inventory;

    @OneToMany(mappedBy = "variant")
    private Set<CartItem> cartItems;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToMany
    @JoinTable(
            name = "variant_attribute_values",
            joinColumns = @JoinColumn(name = "variant_id"),
            inverseJoinColumns = @JoinColumn(name = "attribute_value_id")
    )
    private Set<AttributeValue> values;

    public Variant(VariantRequest request) {
        this.id = request.id();
        this.sku = request.sku();
        this.unlimitedQuantity = request.unlimitedQuantity();
    }
}
