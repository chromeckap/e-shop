package com.ecommerce.cod;

import com.ecommerce.payment.Payment;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class CODPayment extends Payment {
    // add attributes if needed
}
