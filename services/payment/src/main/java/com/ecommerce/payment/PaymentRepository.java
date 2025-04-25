package com.ecommerce.payment;

import com.ecommerce.stripe.StripePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Finds a payment by its Stripe session ID.
     * This method is used to retrieve a payment linked to a specific Stripe session.
     *
     * @param sessionId the Stripe session ID to look up the payment.
     * @return the StripePayment corresponding to the given sessionId.
     */
    StripePayment findPaymentBySessionId(String sessionId);

    Optional<Payment> findByOrderId(Long orderId);
}
