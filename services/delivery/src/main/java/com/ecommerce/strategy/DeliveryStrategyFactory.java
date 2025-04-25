package com.ecommerce.strategy;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class DeliveryStrategyFactory {
    private final ApplicationContext applicationContext;
    private Map<CourierType, DeliveryStrategy> strategies;

    @PostConstruct
    public void init() {
        strategies = applicationContext.getBeansWithAnnotation(CourierHandler.class).entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getValue().getClass().getAnnotation(CourierHandler.class).value(),
                        entry -> (DeliveryStrategy) entry.getValue()
                ));
    }

    public DeliveryStrategy getStrategy(CourierType type) {
        DeliveryStrategy strategy = Optional.ofNullable(strategies.get(type))
                .orElseThrow(() -> new IllegalArgumentException("No delivery strategy found for method type: " + type));

        DeliveryStrategy typedStrategy = (DeliveryStrategy) strategy;
        return typedStrategy;
    }
}
