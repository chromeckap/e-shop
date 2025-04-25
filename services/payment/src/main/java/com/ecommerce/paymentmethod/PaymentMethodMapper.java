package com.ecommerce.paymentmethod;

import com.ecommerce.strategy.PaymentGatewayType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentMethodMapper {

    PaymentMethod toPaymentMethod(PaymentMethodRequest request) {
        log.debug("Mapping PaymentMethodRequest to PaymentMethod: {}", request);
        return PaymentMethod.builder()
                .name(request.name())
                .gatewayType(request.type())
                .isActive(request.isActive())
                .price(request.price())
                .isFreeForOrderAbove(request.isFreeForOrderAbove())
                .freeForOrderAbove(request.freeForOrderAbove())
                .build();
    }

    PaymentMethodResponse toResponse(PaymentMethod paymentMethod) {
        log.debug("Mapping PaymentMethod to PaymentMethodResponse: {}", paymentMethod);
        return PaymentMethodResponse.builder()
                .id(paymentMethod.getId())
                .name(paymentMethod.getName())
                .gatewayType(PaymentGatewayType.getType(paymentMethod))
                .isActive(paymentMethod.isActive())
                .price(paymentMethod.getPrice())
                .isFreeForOrderAbove(paymentMethod.isFreeForOrderAbove())
                .freeForOrderAbove(paymentMethod.getFreeForOrderAbove())
                .build();
    }
}
