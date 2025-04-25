package com.ecommerce.strategy;

import com.ecommerce.payment.Payment;
import com.stripe.exception.StripeException;

public interface PaymentStrategy<T extends Payment> {
    void processPayment(T payment);
    String getPaymentUrlById(Long id) throws StripeException;
}
