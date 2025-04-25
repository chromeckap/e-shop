package com.ecommerce.paymentmethod;

import com.ecommerce.payment.Payment;
import com.ecommerce.strategy.PaymentGatewayType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "payment_method")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentGatewayType gatewayType;

    @Column(nullable = false)
    private boolean isActive;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean isFreeForOrderAbove;

    @Column(precision = 10, scale = 2)
    private BigDecimal freeForOrderAbove;

    @OneToMany(mappedBy = "method", fetch = FetchType.LAZY)
    private Set<Payment> payments;
}
