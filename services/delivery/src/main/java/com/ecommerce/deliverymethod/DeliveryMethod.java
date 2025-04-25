package com.ecommerce.deliverymethod;

import com.ecommerce.delivery.Delivery;
import com.ecommerce.strategy.CourierType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "delivery_method")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DeliveryMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourierType courierType;

    @Column(nullable = false)
    private boolean isActive;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean isFreeForOrderAbove;

    @Column(precision = 10, scale = 2)
    private BigDecimal freeForOrderAbove;

    @OneToMany(mappedBy = "method", fetch = FetchType.LAZY)
    private Set<Delivery> deliveries;
}
