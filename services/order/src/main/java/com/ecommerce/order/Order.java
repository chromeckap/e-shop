package com.ecommerce.order;

import com.ecommerce.orderitem.OrderItem;
import com.ecommerce.userdetails.UserDetails;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "orders")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private UserDetails userDetails;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "order_additional_cost",
            joinColumns = @JoinColumn(name = "order_id")
    )
    @MapKeyColumn(name = "cost_type")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "cost_amount", precision = 10, scale = 2)
    @Builder.Default
    private Map<String, BigDecimal> additionalCosts = new HashMap<>();

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createTime;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updateDate;

    /**
     * Adds an order item to the order.
     *
     * @param item the order item to add
     */
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }

    /**
     * Adds other costs to the order.
     *
     * @param name the name of cost
     * @param price   the cost amount
     */
    public void addAdditionalCost(String name, BigDecimal price) {
        additionalCosts.put(name, price);
    }

    /**
     * Calculates the total price of the order by summing the total prices of order items and additional costs.
     *
     * @return the total price of the order
     */
    public BigDecimal calculateTotalPrice() {
        BigDecimal itemsTotal = orderItems.stream()
                .map(OrderItem::calculateTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal additionalCostsTotal = additionalCosts.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return itemsTotal.add(additionalCostsTotal);
    }
}
