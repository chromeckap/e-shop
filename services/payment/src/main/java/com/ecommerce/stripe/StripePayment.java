package com.ecommerce.stripe;

import com.ecommerce.payment.Payment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class StripePayment extends Payment {

    @Column(nullable = false, unique = true)
    private String sessionId;

    @Column(nullable = false)
    private boolean isPaid;
}
