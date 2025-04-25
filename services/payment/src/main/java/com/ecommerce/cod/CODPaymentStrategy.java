package com.ecommerce.cod;

import com.ecommerce.payment.PaymentStatus;
import com.ecommerce.strategy.PaymentStrategy;
import com.ecommerce.strategy.PaymentGatewayHandler;
import com.ecommerce.strategy.PaymentGatewayType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@PaymentGatewayHandler(PaymentGatewayType.CASH_ON_DELIVERY)
public class CODPaymentStrategy implements PaymentStrategy<CODPayment> {

    /**
     * Processes a Cash on Delivery payment.
     *
     * @param payment The COD payment to be processed.
     * @throws NullPointerException if `payment` is null.
     */
    @Override
    public void processPayment(CODPayment payment) {
        Objects.requireNonNull(payment, "Platba nesmí být prázdná.");

        payment.setStatus(PaymentStatus.CASH_ON_DELIVERY);
        log.info("Cash on Delivery payment successfully created. ID: {}", payment.getId());
    }

    /**
     * URL retrieval is not supported for Cash on Delivery payments.
     *
     * @param id Payment ID
     * @throws UnsupportedOperationException Always throws an exception.
     */
    @Override
    public String getPaymentUrlById(Long id) {
        throw new UnsupportedOperationException("Pro dobírku nelze získat URL.");
    }
}
