package com.ecommerce.payment;

import com.ecommerce.cod.CODPayment;
import com.ecommerce.strategy.PaymentGatewayType;
import com.ecommerce.stripe.StripePayment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentMapper {

    public Payment toPayment(PaymentRequest request, PaymentGatewayType type) {
        Payment payment = this.createPaymentInstance(type);
        payment.setOrderId(request.orderId());
        payment.setTotalPrice(request.totalPrice());

        return payment;
    }

    private Payment createPaymentInstance(PaymentGatewayType paymentGatewayType) {
        return switch (paymentGatewayType) {
            case STRIPE_CARD -> StripePayment.builder().build();
            case CASH_ON_DELIVERY -> CODPayment.builder().build();
        };
    }

    public PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .totalPrice(payment.getTotalPrice())
                .status(payment.getStatus().name())
                .build();
    }
}