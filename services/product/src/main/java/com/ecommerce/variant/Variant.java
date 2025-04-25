package com.ecommerce.variant;

import com.ecommerce.attributevalue.AttributeValue;
import com.ecommerce.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "variant")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Variant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal discountedPrice;

    @Column(nullable = false)
    @Min(0)
    private int quantity;

    @Column(nullable = false)
    private boolean quantityUnlimited;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "variant_attribute_value",
            joinColumns = @JoinColumn(name = "variant_id"),
            inverseJoinColumns = @JoinColumn(name = "attribute_value_id")
    )
    @Builder.Default
    private List<AttributeValue> values = new ArrayList<>();

    /**
     * Returns the effective price of the variant, considering discounts.
     *
     * @return the effective price.
     */
    public BigDecimal getPrice() {
        return (discountedPrice != null && discountedPrice.compareTo(BigDecimal.ZERO) > 0)
                ? discountedPrice : basePrice;
    }

}
