package com.ecommerce.strategy;

import com.ecommerce.payment.Payment;
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
public class PaymentStrategyFactory {
    private final ApplicationContext applicationContext;
    private Map<PaymentGatewayType, PaymentStrategy<? extends Payment>> strategies;

    @PostConstruct
    public void init() {
        strategies = applicationContext.getBeansWithAnnotation(PaymentGatewayHandler.class).entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getValue().getClass().getAnnotation(PaymentGatewayHandler.class).value(),
                        entry -> (PaymentStrategy<?>) entry.getValue()
                ));
    }

    public <T extends Payment> PaymentStrategy<T> getStrategy(PaymentGatewayType type) {
        PaymentStrategy<?> strategy = Optional.ofNullable(strategies.get(type))
                .orElseThrow(() -> new IllegalArgumentException("No payment strategy found for method type: " + type));

        @SuppressWarnings("unchecked")
        PaymentStrategy<T> typedStrategy = (PaymentStrategy<T>) strategy;
        return typedStrategy;
    }
}


