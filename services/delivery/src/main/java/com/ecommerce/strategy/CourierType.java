package com.ecommerce.strategy;

import com.ecommerce.deliverymethod.DeliveryMethod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum CourierType {
    PACKETA("Zásilkovna"),
    BALIKOVNA("Balíkovna"),
    OTHER("Ostatní");

    private final String name;

    public static List<Map<String, String>> getAll() {
        return Arrays.stream(values())
                .map(type -> Map.of(
                        "type", type.name(),
                        "name", type.getName()
                ))
                .toList();
    }

    public static Map<String, String> getType(DeliveryMethod deliveryMethod) {
        CourierType type = deliveryMethod.getCourierType();
        return Map.of(
                "type", type.name(),
                "name", type.getName()
        );
    }

}