package eshop.backend.model;

import eshop.backend.enums.DiscountType;
import eshop.backend.request.CouponRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private BigDecimal discount;

    @Enumerated(EnumType.STRING)
    private DiscountType type;

    private BigDecimal minimumOrderAmount;

    private LocalDateTime expirationDate;

    private int uses;

    public Coupon(CouponRequest request) {
        this.code = request.code();
        this.discount = request.discount();
        this.type = request.type();
        this.expirationDate = request.expirationDate();
        this.uses = request.uses();
    }
}