package com.ecommerce.orderitem;

import com.ecommerce.order.Order;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.Map;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "order_item")
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    @Min(0)
    private int quantity;

    @ElementCollection
    @CollectionTable(
            name = "order_item_attribute_value",
            joinColumns = @JoinColumn(name = "order_item_id")
    )
    @Column(name = "attribute_value")
    private Map<String, String> values;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;


    /**
     * Calculates the total price for this order item.
     *
     * @return the total price as (price * quantity)
     */
    public BigDecimal calculateTotalPrice() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
