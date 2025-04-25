package com.ecommerce.strategy;

import com.ecommerce.paymentmethod.PaymentMethod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public enum PaymentGatewayType {
    STRIPE_CARD("Stripe karta"),
    CASH_ON_DELIVERY("Dobírka");

    private final String name;

    public static List<Map<String, String>> getAll() {
        return Arrays.stream(values())
                .map(gatewayType -> Map.of(
                        "type", gatewayType.name(),
                        "name", gatewayType.getName()
                ))
                .toList();
    }

    public static Map<String, String> getType(PaymentMethod paymentMethod) {
        PaymentGatewayType type = paymentMethod.getGatewayType();
        return Map.of(
                "type", type.name(),
                "name", type.getName()
        );
    }
}
