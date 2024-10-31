package eshop.backend.model;

import eshop.backend.enums.InventoryAction;
import eshop.backend.request.InventoryRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "inventory_history")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "variant_id")
    private Variant variant;

    @Enumerated(EnumType.STRING)
    private InventoryAction inventoryAction;

    @Min(value = 0, message = "Quantity must be at least 0.")
    private Integer quantity;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public Inventory(Variant variant, int quantity, InventoryAction inventoryAction) {
        this.variant = variant;
        this.quantity = quantity;
        this.inventoryAction = inventoryAction;
    }

    public Inventory(InventoryRequest request) {
        this.inventoryAction = request.inventoryAction();
        this.quantity = request.quantity();
        this.createdAt = request.createdAt();
    }
}