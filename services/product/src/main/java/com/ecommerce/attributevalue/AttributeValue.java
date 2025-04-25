package com.ecommerce.attributevalue;

import com.ecommerce.attribute.Attribute;
import com.ecommerce.variant.Variant;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "attribute_value")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AttributeValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    private Attribute attribute;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "variant_attribute_value",
            joinColumns = @JoinColumn(name = "attribute_value_id"),
            inverseJoinColumns = @JoinColumn(name = "variant_id")
    )
    @Builder.Default
    private List<Variant> variants = new ArrayList<>();

}
