package com.ecommerce.attribute;

import com.ecommerce.attributevalue.AttributeValue;
import com.ecommerce.product.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "attribute")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Attribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @OneToMany(
            mappedBy = "attribute",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<AttributeValue> values = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "product_attribute",
            joinColumns = @JoinColumn(name = "attribute_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    @Builder.Default
    private List<Product> products = new ArrayList<>();

}