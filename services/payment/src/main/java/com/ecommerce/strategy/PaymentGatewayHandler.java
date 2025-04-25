package com.ecommerce.strategy;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface PaymentGatewayHandler {
    PaymentGatewayType value();
}
