package com.ecommerce.product;

import com.ecommerce.attribute.Attribute;
import com.ecommerce.productimage.ProductImage;
import com.ecommerce.variant.Variant;
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
@Table(name = "product")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean isVisible;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "product_category",
            joinColumns = @JoinColumn(name = "product_id")
    )
    @Column(name = "category_id")
    @Builder.Default
    private Set<Long> categoryIds = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OrderBy("uploadOrder ASC")
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @Builder.Default
    private List<Variant> variants = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_related_product",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "related_product_id")
    )
    @Builder.Default
    private Set<Product> relatedProducts = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_attribute",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "attribute_id")
    )
    @Builder.Default
    private Set<Attribute> attributes = new HashSet<>();

}
