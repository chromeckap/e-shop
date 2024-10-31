package eshop.backend.model;


import eshop.backend.enums.DiscountType;
import eshop.backend.request.DiscountRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "discount")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Min(value = 0, message = "Amount must be at least 0.")
    private double amount;

    @Enumerated(EnumType.STRING)
    private DiscountType type;

    @CreationTimestamp
    private LocalDateTime startDate;

    @UpdateTimestamp
    private LocalDateTime endDate;

    @OneToMany(mappedBy = "discount")
    private Set<Category> categories;

    public Discount(DiscountRequest request) {
        this.id = request.id();
        this.name = request.name();
        this.amount = request.amount();
        this.type = request.type();
        this.startDate = request.startDate();
        this.endDate = request.endDate();
    }

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startDate) && now.isBefore(endDate);
    }
}
